package Models;

import java.util.Map;

public class NutritionalGoal {
    private String nutrient;
    private float delta;
    private boolean increase; 

    public NutritionalGoal(String nutrient, float delta, boolean increase) {
        this.nutrient = nutrient;
        this.delta = delta;
        this.increase = increase;
    }

    public boolean isGoalMet(Map<String, Float> original, Map<String, Float> alternative) {
        float orig = original.getOrDefault(nutrient, 0f);
        float alt = alternative.getOrDefault(nutrient, 0f);
        if (increase) {
            return alt >= orig + delta;
        } else {
            return alt <= orig - delta;
        }
    }

    public String getNutrient() {
        return nutrient;
    }

    public float getDelta() {
        return delta;
    }

    public boolean isIncrease() {
        return increase;
    }
}
