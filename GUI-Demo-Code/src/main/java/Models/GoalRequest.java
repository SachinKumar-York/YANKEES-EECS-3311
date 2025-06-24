package Models;

import java.util.List;

public class GoalRequest {
    private List<NutritionalGoal> goals;

    public GoalRequest(List<NutritionalGoal> goals) {
        this.goals = goals;
    }

    public List<NutritionalGoal> getGoals() {
        return goals;
    }
}