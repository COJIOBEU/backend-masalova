package tracker;

import java.util.UUID;

public class Project {
    private String id;
    private String name;
    private boolean isActive;
    private int openTasksCount;

    public Project(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.isActive = true;
        this.openTasksCount = 0;
    }

    public Project() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public int getOpenTasksCount() { return openTasksCount; }
    public void incrementOpenTasks() { this.openTasksCount++; }
    public void decrementOpenTasks() {
        if (this.openTasksCount > 0) this.openTasksCount--;
    }
}