package tracker;

import java.util.UUID;

public class Project {
    private String id;
    private String name;
    private boolean isActive;
    private int openTasksCount;

    public Project(String name) {
        this.id = UUID.randomUUID().toString();
        setName(name);
        this.isActive = true;
        this.openTasksCount = 0;
    }

    public Project() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { 
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название проекта не может быть пустым");
        }
        this.name = name; 
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { 
        if (!active && openTasksCount > 0) {
            throw new IllegalStateException("Нельзя деактивировать проект с открытыми задачами");
        }
        this.isActive = active; 
    }
    
    public int getOpenTasksCount() { return openTasksCount; }
    public void incrementOpenTasks() { 
        if (!isActive) {
            throw new IllegalStateException("Нельзя создать задачу в неактивном проекте");
        }
        this.openTasksCount++; 
    }
    public void decrementOpenTasks() {
        if (openTasksCount <= 0) {
            throw new IllegalStateException("Счётчик задач не может быть отрицательным");
        }
        openTasksCount--;
    }

    public void validateCanBeDeleted() {
        if (openTasksCount > 0) {
            throw new IllegalStateException("Нельзя удалить проект с открытыми задачами");
        }
    }
}