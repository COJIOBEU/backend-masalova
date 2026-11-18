package tracker;

public class FeatureTask extends Task implements Rejectable{
    private int storyPoints;

    public FeatureTask(String title, String projectId, int storyPoints) {
        super(title, projectId);
        setStoryPoints(storyPoints);
    }

    public FeatureTask() {}

    public int getStoryPoints() { return storyPoints; }
    public void setStoryPoints(int storyPoints) { 
        if (storyPoints <= 0) {
            throw new IllegalArgumentException("Для фичи нужно указать storyPoints > 0");
        }
        this.storyPoints = storyPoints; 
    }

    @Override
    public String getType() {
        return "FeatureTask";
    }

    @Override
    public void onComplete(){
        switch (this.status) {
            case OPEN:
                this.status = TaskStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                this.status = TaskStatus.TESTING;
                break;
            case TESTING:
                this.status = TaskStatus.DONE;
                break;
            case DONE:
                throw new IllegalStateException("Задача уже завершена");
            default:
                throw new IllegalStateException("Неизвестный статус задачи");
        }
    }

    @Override 
    public void rejectTask(){
        if (this.status == TaskStatus.TESTING){
            this.status = TaskStatus.IN_PROGRESS;
        } else {
            throw new IllegalStateException("Вернуть на доработку можно только задачу на тестировании");
        }
    }
}