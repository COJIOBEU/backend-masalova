package tracker;

public class FeatureTask extends Task {
    private int storyPoints;

    public FeatureTask(String title, String projectId, int storyPoints) {
        super(title, projectId);
        this.storyPoints = storyPoints;
    }

    public FeatureTask() {}

    public int getStoryPoints() { return storyPoints; }
    public void setStoryPoints(int storyPoints) { this.storyPoints = storyPoints; }

    @Override
    public String getType() {
        return "FeatureTask";
    }

    @Override
    public void onComplete(Project project) {
        this.status = "DONE";
        project.decrementOpenTasks();
    }
}