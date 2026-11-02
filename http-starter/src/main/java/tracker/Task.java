package tracker;

import java.util.UUID;

public abstract class Task {
    protected String id;
    protected String title;
    protected String status;
    protected String projectId;

    public Task(String title, String projectId) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.status = "OPEN";
        this.projectId = projectId;
    }

    public Task() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public abstract void onComplete(Project project);

    public abstract String getType();
}