//SwapValidator.java start

package Models;

import java.util.Map;
import java.util.Set;

public class SwapValidator {
    private static final Map<String, String> DISPLAY_NUTRIENT_MAP = Map.of(
        "TDF", "Fibre", "KCAL", "Calories", "FAT", "Fat", "PROT", "Protein", "CARB", "Carbohydrate"
    );

    public boolean isSwapValid(Map<String, Float> original, Map<String, Float> alternative,
                               Set<String> goalNutrients, GoalComponent goalComponent) {

        for (String symbol : DISPLAY_NUTRIENT_MAP.keySet()) {
            if (goalNutrients.contains(symbol)) continue;

            float orig = original.getOrDefault(symbol, 0f);
            float alt = alternative.getOrDefault(symbol, 0f);

            if (orig == 0f && alt == 0f) continue;
            if (orig == 0f || Math.abs(alt - orig) / orig > 0.10f) {
                return false;
            }
        }
        return goalComponent.isGoalMet(original, alternative);
    }
}

