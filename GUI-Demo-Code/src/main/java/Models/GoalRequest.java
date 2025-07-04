package Models;

public class GoalRequest {
    private GoalComponent goalComponent;

    public GoalRequest(GoalComponent goalComponent) {
        this.goalComponent = goalComponent;
    }

    public GoalComponent getGoalComponent() {
        return goalComponent;
    }
}
