package tracker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class TrackerHandler implements HttpHandler {
    private static final int MAX_BODY_SIZE = 1_000_000;

    private final TrackerService service;
    private final JsonMapper mapper;

    public TrackerHandler(TrackerService service) {
        this.service = service;
        this.mapper = JsonMapper.builder().build();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (path.equals("/projects") && method.equals("POST")) {
                createProject(exchange);
            } else if (path.equals("/projects") && method.equals("GET")) {
                getProjects(exchange);
            } else if (path.matches("/projects/[^/]+") && method.equals("GET")) {
                getProjectById(exchange);
            } else if (path.matches("/projects/[^/]+") && method.equals("PUT")) {
                updateProject(exchange);
            } else if (path.matches("/projects/[^/]+") && method.equals("DELETE")) {
                deleteProject(exchange);
            } else if (path.equals("/tasks") && method.equals("POST")) {
                createTask(exchange);
            } else if (path.equals("/tasks") && method.equals("GET")) {
                getTasks(exchange);
            } else if (path.matches("/tasks/[^/]+/complete") && method.equals("POST")) {
                completeTask(exchange);
            } else if (path.matches("/tasks/[^/]+/reject") && method.equals("POST")) {
                rejectTask(exchange);
            } else if (path.matches("/tasks/[^/]+") && method.equals("GET")) {
                getTaskById(exchange);
            } else if (path.matches("/tasks/[^/]+") && method.equals("PUT")) {
                updateTask(exchange);
            } else if (path.matches("/tasks/[^/]+") && method.equals("DELETE")) {
                deleteTask(exchange);
            } else if (isKnownPath(path)) {
                sendError(exchange, 405, "Method not allowed");
            } else {
                sendError(exchange, 404, "Endpoint not found");
            }

        } catch (Exception e) {

            boolean jsonError = e instanceof JacksonException;

            if (e instanceof TrackerNotFoundException) {
                sendError(exchange, 404, e.getMessage());

            } else if (jsonError || e instanceof IllegalArgumentException) {
                sendError(
                    exchange,
                    400,
                    jsonError ? "Invalid JSON or field value" : e.getMessage()
                );

            } else if (e instanceof IllegalStateException) {
                sendError(exchange, 409, e.getMessage());

            } else {
                sendError(exchange, 500, "Internal server error");
            }

        } finally {
            exchange.close();
        }
    }

    private boolean isKnownPath(String path) {
        return path.equals("/projects")
            || path.matches("/projects/[^/]+")
            || path.equals("/tasks")
            || path.matches("/tasks/[^/]+")
            || path.matches("/tasks/[^/]+/complete")
            || path.matches("/tasks/[^/]+/reject");
    }

    private JsonNode readJson(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        JsonNode json = mapper.readTree(body);

        if (json == null || !json.isObject()) {
            throw new IllegalArgumentException(
                "Тело запроса должно быть JSON-объектом"
            );
        }

        return json;
    }

    private String requiredText(JsonNode json, String field) {
        JsonNode value = json.get(field);

        if (value == null || !value.isTextual()
                || value.asText().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Поле %s обязательно и должно быть непустой строкой".formatted(field)
            );
        }

        return value.asText();
    }

    private String optionalText(JsonNode json, String field) {
        JsonNode value = json.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        if (!value.isTextual()) {
            throw new IllegalArgumentException(
                "Поле %s должно быть строкой".formatted(field)
            );
        }

        return value.asText();
    }

    private Integer optionalInteger(JsonNode json, String field) {
        JsonNode value = json.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        if (!value.isIntegralNumber() || !value.canConvertToInt()) {
            throw new IllegalArgumentException(
                "Поле %s должно быть целым числом".formatted(field)
            );
        }

        return value.asInt();
    }

    private void createProject(HttpExchange exchange) throws IOException {
        JsonNode json = readJson(exchange);

        Project project = service.createProject(
            requiredText(json, "name")
        );

        sendJson(exchange, 201, project);
    }

    private void getProjects(HttpExchange exchange) throws IOException {
        sendJson(exchange, 200, service.getProjects());
    }

    private void getProjectById(HttpExchange exchange) throws IOException {
        sendJson(
            exchange,
            200,
            service.getProject(extractId(exchange, 2))
        );
    }

    private void updateProject(HttpExchange exchange) throws IOException {
        JsonNode json = readJson(exchange);

        Project project = service.updateProject(
            extractId(exchange, 2),
            requiredText(json, "name")
        );

        sendJson(exchange, 200, project);
    }

    private void deleteProject(HttpExchange exchange) throws IOException {
        service.deleteProject(extractId(exchange, 2));
        sendEmpty(exchange, 204);
    }

    private void createTask(HttpExchange exchange) throws IOException {
        JsonNode json = readJson(exchange);

        String title = requiredText(json, "title");
        String projectId = requiredText(json, "projectId");
        String type = requiredText(json, "type");
        String severity = optionalText(json, "severity");
        Integer storyPoints = optionalInteger(json, "storyPoints");

        Task task = service.createTask(
            title, projectId, type, severity, storyPoints
        );

        sendJson(exchange, 201, task);
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(
            exchange.getRequestURI().getRawQuery()
        );

        for (String key : params.keySet()) {
            if (!key.equals("projectId")
                    && !key.equals("type")
                    && !key.equals("status")) {
                throw new IllegalArgumentException(
                    "Неизвестный параметр фильтра: %s".formatted(key)
                );
            }
        }

        String statusValue = params.get("status");
        TaskStatus status = null;

        if (statusValue != null) {
            try {
                status = TaskStatus.valueOf(statusValue);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Неизвестный статус: %s".formatted(statusValue)
                );
            }
        }

        List<Task> tasks = service.getTasks(
            params.get("projectId"),
            params.get("type"),
            status
        );

        sendJson(exchange, 200, tasks);
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        sendJson(
            exchange,
            200,
            service.getTask(extractId(exchange, 2))
        );
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        JsonNode json = readJson(exchange);

        String title = optionalText(json, "title");
        String severity = optionalText(json, "severity");
        Integer storyPoints = optionalInteger(json, "storyPoints");

        if (title == null && severity == null && storyPoints == null) {
            throw new IllegalArgumentException(
                "Нужно передать хотя бы одно поле для изменения"
            );
        }

        if (title != null && title.isBlank()) {
            throw new IllegalArgumentException(
                "Название задачи не может быть пустым"
            );
        }

        Task task = service.updateTask(
            extractId(exchange, 2),
            title,
            severity,
            storyPoints
        );

        sendJson(exchange, 200, task);
    }

    private void completeTask(HttpExchange exchange) throws IOException {
        sendJson(
            exchange,
            200,
            service.completeTask(extractId(exchange, 2))
        );
    }

    private void rejectTask(HttpExchange exchange) throws IOException {
        sendJson(
            exchange,
            200,
            service.rejectTask(extractId(exchange, 2))
        );
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        service.deleteTask(extractId(exchange, 2));
        sendEmpty(exchange, 204);
    }

    private String extractId(HttpExchange exchange, int segment) {
        String[] parts = exchange.getRequestURI().getPath().split("/");

        if (parts.length <= segment || parts[segment].isEmpty()) {
            throw new IllegalArgumentException(
                "Не указан идентификатор ресурса"
            );
        }

        return parts[segment];
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return result;
        }

        for (String part : query.split("&")) {
            if (part.isEmpty()) {
                continue;
            }

            String[] pair = part.split("=", 2);

            String key = URLDecoder.decode(
                pair[0],
                StandardCharsets.UTF_8
            );

            String value = pair.length == 2
                ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                : "";

            if (result.putIfAbsent(key, value) != null) {
                throw new IllegalArgumentException(
                    "Параметр повторяется: %s".formatted(key)
                );
            }
        }

        return result;
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int total = 0;
            int read;

            while ((read = input.read(buffer)) != -1) {
                total += read;

                if (total > MAX_BODY_SIZE) {
                    throw new IllegalArgumentException(
                        "Тело запроса слишком большое"
                    );
                }

                output.write(buffer, 0, read);
            }

            return output.toString(StandardCharsets.UTF_8);
        }
    }

    private void sendJson(
            HttpExchange exchange,
            int status,
            Object value
    ) throws IOException {

        byte[] bytes = mapper.writeValueAsBytes(value);

        exchange.getResponseHeaders().set(
            "Content-Type",
            "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private void sendError(
            HttpExchange exchange,
            int status,
            String message
    ) throws IOException {

        Map<String, String> error = new HashMap<>();
        error.put(
            "error",
            message == null ? "Request failed" : message
        );

        sendJson(exchange, status, error);
    }

    private void sendEmpty(
            HttpExchange exchange,
            int status
    ) throws IOException {
        exchange.sendResponseHeaders(status, -1);
    }
}
