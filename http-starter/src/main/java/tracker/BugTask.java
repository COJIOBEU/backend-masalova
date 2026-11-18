package tracker;

public class BugTask extends Task {
    private String severity;

    public BugTask(String title, String projectId, String severity) {
        super(title, projectId);
        setSeverity(severity);
    }

    public BugTask() {}

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { 
        if (severity == null || severity.trim().isEmpty()) {
            throw new IllegalArgumentException("Для бага нужно указать severity");
        }
        this.severity = severity; 
    }

    @Override
    public String getType() {
        return "BugTask";
    }

    @Override
    public void onComplete(){
        if (this.status != TaskStatus.OPEN){
            throw new IllegalStateException("Нельзя завершить не открытую задачу");
        }
        this.status = TaskStatus.DONE;
    }
}