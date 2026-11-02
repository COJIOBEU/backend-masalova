package tracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class TrackerHandler implements HttpHandler {
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
            } else if (path.equals("/tasks") && method.equals("POST")) {
                createTask(exchange);
            } else if (path.equals("/tasks") && method.equals("GET")) {
                getTasks(exchange);
            } else if (path.matches("/tasks/[^/]+/complete") && method.equals("POST")) {
                completeTask(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Endpoint not found\"}");
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (IllegalStateException e) {
            sendResponse(exchange, 409, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\": \"Internal server error\"}");
        }
    }

    private void createProject(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        JsonNode json = mapper.readTree(body);
        String name = json.get("name").asText();
        
        Project project = service.createProject(name);
        sendResponse(exchange, 201, mapper.writeValueAsString(project));
    }

    private void getProjects(HttpExchange exchange) throws IOException {
        List<Project> projects = service.getProjects();
        sendResponse(exchange, 200, mapper.writeValueAsString(projects));
    }

    private void createTask(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        JsonNode json = mapper.readTree(body);
        
        String title = json.get("title").asText();
        String projectId = json.get("projectId").asText();
        String type = json.get("type").asText();
        
        String severity = json.has("severity") ? json.get("severity").asText() : null;
        Integer storyPoints = json.has("storyPoints") ? json.get("storyPoints").asInt() : null;

        Task task = service.createTask(title, projectId, type, severity, storyPoints);
        sendResponse(exchange, 201, mapper.writeValueAsString(task));
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String projectId = null, type = null, status = null;
        
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("projectId")) projectId = pair[1];
                    if (pair[0].equals("type")) type = pair[1];
                    if (pair[0].equals("status")) status = pair[1];
                }
            }
        }
        
        List<Task> tasks = service.getTasks(projectId, type, status);
        sendResponse(exchange, 200, mapper.writeValueAsString(tasks));
    }

    private void completeTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String taskId = path.split("/")[2];
        
        Task task = service.completeTask(taskId);
        sendResponse(exchange, 200, mapper.writeValueAsString(task));
    }

    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}