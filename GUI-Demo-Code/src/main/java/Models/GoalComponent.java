package Models;

import java.util.Map;

public interface GoalComponent {
    boolean isGoalMet(Map<String, Float> original, Map<String, Float> alternative);
}

