package Models;

import java.util.*;
import java.util.stream.Collectors;

public class CompositeGoal implements GoalComponent {
    private static final int MAX_GOALS = 2;
    private final List<GoalComponent> subGoals = new ArrayList<>();

    public void addGoal(GoalComponent goal) {
        if (subGoals.size() >= MAX_GOALS) {
            throw new IllegalStateException("Cannot add more than " + MAX_GOALS + " goals to a composite goal.");
        }
        subGoals.add(goal);
    }

    @Override
    public boolean isGoalMet(Map<String, Float> original, Map<String, Float> alternative) {
        for (GoalComponent goal : subGoals) {
            if (!goal.isGoalMet(original, alternative)) {
                return false;
            }
        }
        return true;
    }

    public List<GoalComponent> getSubGoals() {
        return subGoals;
    }

    @Override
    public Set<String> getTargetNutrients() {
        return subGoals.stream()
                .flatMap(gc -> gc.getTargetNutrients().stream())
                .collect(Collectors.toSet());
    }
}
