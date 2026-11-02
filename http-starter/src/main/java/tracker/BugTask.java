package tracker;

public class BugTask extends Task {
    private String severity;

    public BugTask(String title, String projectId, String severity) {
        super(title, projectId);
        this.severity = severity;
    }

    public BugTask() {}

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    @Override
    public String getType() {
        return "BugTask";
    }

    @Override
    public void onComplete(Project project) {
        this.status = "DONE";
        project.decrementOpenTasks();
    }
}