package tracker;

import java.util.UUID;

public abstract class Task {
    protected String id;
    protected String title;
    protected TaskStatus status;
    protected String projectId;

    public Task(String title, String projectId) {
        this.id = UUID.randomUUID().toString();
        setTitle(title);
        this.status = TaskStatus.OPEN;
        setProjectId(projectId);
    }

    public Task() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { 
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }
        this.title = title; 
    }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                "Статус задачи не может быть null"
            );
        }
        this.status = status;
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException(
                "ID проекта не может быть пустым"
            );
        }
        this.projectId = projectId;
    }

    public abstract void onComplete();

    public abstract String getType();
}