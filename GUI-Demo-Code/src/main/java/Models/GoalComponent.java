package Models;

import java.util.Map;
import java.util.Set;

public interface GoalComponent {
    boolean isGoalMet(Map<String, Float> original, Map<String, Float> alternative);
    Set<String> getTargetNutrients();  
}

