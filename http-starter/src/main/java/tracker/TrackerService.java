
package tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackerService {
    private final Map<String, Project> projects = new HashMap<>();
    private final Map<String, Task> tasks = new HashMap<>();

    public Project createProject(String name) {
        Project project = new Project(name);
        projects.put(project.getId(), project);
        return project;
    }

    public Project getProject(String projectId) {
        Project project = projects.get(projectId);

        if (project == null) {
            throw new TrackerNotFoundException(
                "Проект не найден: %s".formatted(projectId)
            );
        }

        return project;
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projects.values());
    }

    public Project updateProject(String projectId, String newName) {
        Project project = getProject(projectId);
        project.setName(newName);

        return project;
    }

    public void deleteProject(String projectId) {
        Project project = getProject(projectId);
        project.validateCanBeDeleted();

        projects.remove(projectId);
    }

    public Task createTask(String title, String projectId, String type,
                           String severity, Integer storyPoints) {
        Project project = getProject(projectId);

        if (!project.isActive()) {
            throw new IllegalStateException(
                "Нельзя создать задачу в неактивном проекте"
            );
        }

        Task task;

        if ("BugTask".equals(type)) {
            if (severity == null || severity.isBlank()) {
                throw new IllegalArgumentException(
                    "Для BugTask обязательно поле severity"
                );
            }

            task = new BugTask(title, projectId, severity);

        } else if ("FeatureTask".equals(type)) {
            if (storyPoints == null) {
                throw new IllegalArgumentException(
                    "Для FeatureTask обязательно поле storyPoints"
                );
            }

            task = new FeatureTask(title, projectId, storyPoints);

        } else {
            throw new IllegalArgumentException(
                "Неизвестный тип задачи: %s".formatted(type)
            );
        }

        project.incrementOpenTasks();
        tasks.put(task.getId(), task);

        return task;
    }

    public Task getTask(String taskId) {
        Task task = tasks.get(taskId);

        if (task == null) {
            throw new TrackerNotFoundException(
                "Задача не найдена: %s".formatted(taskId)
            );
        }

        return task;
    }

    public List<Task> getTasks(String projectId, String type,
                               TaskStatus status) {
        List<Task> result = new ArrayList<>();

        for (Task task : tasks.values()) {
            if (projectId != null
                    && !projectId.equals(task.getProjectId())) {
                continue;
            }

            if (type != null && !type.equals(task.getType())) {
                continue;
            }

            if (status != null && status != task.getStatus()) {
                continue;
            }

            result.add(task);
        }

        return result;
    }

    public Task completeTask(String taskId) {
        Task task = getTask(taskId);
        task.onComplete();

        if (task.getStatus() == TaskStatus.DONE) {
            Project project = getProject(task.getProjectId());
            project.decrementOpenTasks();
        }

        return task;
    }

    public Task rejectTask(String taskId) {
        Task task = getTask(taskId);

        if (!(task instanceof Rejectable)) {
            throw new IllegalStateException(
                "Этот тип задачи не поддерживает возврат на доработку"
            );
        }

        ((Rejectable) task).rejectTask();

        return task;
    }

    public void deleteTask(String taskId) {
        Task task = getTask(taskId);

        if (task.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException(
                "Нельзя удалить завершённую задачу"
            );
        }

        Project project = getProject(task.getProjectId());
        project.decrementOpenTasks();

        tasks.remove(taskId);
    }

    public Task updateTask(String taskId, String title,
                                     String severity,
                                     Integer storyPoints) {
        Task task = getTask(taskId);

        if (task.getStatus() != TaskStatus.OPEN) {
            throw new IllegalStateException(
                "Можно изменять только задачи со статусом OPEN"
            );
        }

        if (title != null && title.isBlank()) {
            throw new IllegalArgumentException(
                "Название задачи не может быть пустым"
            );
        }

        if (task instanceof BugTask) {
            if (storyPoints != null) {
                throw new IllegalArgumentException(
                    "Для BugTask нельзя изменять storyPoints"
                );
            }

            if (title != null) {
                task.setTitle(title);
            }

            if (severity != null) {
                ((BugTask) task).setSeverity(severity);
            }

        } else if (task instanceof FeatureTask) {
            if (severity != null) {
                throw new IllegalArgumentException(
                    "Для FeatureTask нельзя изменять severity"
                );
            }

            if (title != null) {
                task.setTitle(title);
            }

            if (storyPoints != null) {
                ((FeatureTask) task).setStoryPoints(storyPoints);
            }
        }

        return task;
    }
}
