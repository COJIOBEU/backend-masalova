package tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackerService {
    private final Map<String, Project> projects = new HashMap<>();
    private final Map<String, Task> tasks = new HashMap<>();

    public Project createProject(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название проекта не может быть пустым");
        }
        Project project = new Project(name);
        projects.put(project.getId(), project);
        return project;
    }

    public Project getProject(String projectId) {
        Project project = projects.get(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Проект не найден: " + projectId);
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
        if (project.getOpenTasksCount() > 0) {
            throw new IllegalStateException("Нельзя удалить проект с открытыми задачами");
        }
        projects.remove(projectId);
    }

    public Task createTask(String title, String projectId, String type, String severity, Integer storyPoints) {
        Project project = getProject(projectId);
        
        if (!project.isActive()) {
            throw new IllegalStateException("Нельзя создать задачу в неактивном проекте");
        }

        Task task;
        if ("BugTask".equals(type)) {
            if (severity == null || severity.trim().isEmpty()) {
                throw new IllegalArgumentException("Для бага нужно указать severity");
            }
            task = new BugTask(title, projectId, severity);
        } else if ("FeatureTask".equals(type)) {
            if (storyPoints == null || storyPoints <= 0) {
                throw new IllegalArgumentException("Для фичи нужно указать storyPoints > 0");
            }
            task = new FeatureTask(title, projectId, storyPoints);
        } else {
            throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        tasks.put(task.getId(), task);
        project.incrementOpenTasks();
        return task;
    }

    public Task getTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Задача не найдена: " + taskId);
        }
        return task;
    }

    public List<Task> getTasks(String projectId, String type, String status) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (projectId != null && !projectId.equals(task.getProjectId())) {
                continue;
            }
            if (type != null && !type.equals(task.getType())) {
                continue;
            }
            if (status != null && !status.equals(task.getStatus())) {
                continue;
            }
            result.add(task);
        }
        return result;
    }

    public Task completeTask(String taskId) {
        Task task = getTask(taskId);
        
        if ("DONE".equals(task.getStatus())) {
            throw new IllegalStateException("Задача уже завершена");
        }

        Project project = getProject(task.getProjectId());
        task.onComplete(project);

        return task;
    }

    public void deleteTask(String taskId) {
        Task task = getTask(taskId);
        
        if ("DONE".equals(task.getStatus())) {
            throw new IllegalStateException("Нельзя удалить завершённую задачу");
        }

        Project project = getProject(task.getProjectId());
        project.decrementOpenTasks();
        tasks.remove(taskId);
    }
}