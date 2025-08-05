//NutrientComparator.java start

package Models;

import DAO.NutrientDatabase;
import java.util.*;

public class NutrientComparator {
    private final NutrientDatabase nutrientDatabase = new NutrientDatabase();
    private static final Map<String, String> DISPLAY_NUTRIENT_MAP = Map.of(
        "TDF", "Fibre", "KCAL", "Calories", "FAT", "Fat", "PROT", "Protein", "CARB", "Carbohydrate"
    );

    public Map<String, Float> computeMealNutrients(List<MealIngredient> ingredients) {
        Map<String, Float> totals = new HashMap<>();
        for (MealIngredient mi : ingredients) {
            Map<String, Float> nutrients = nutrientDatabase.getNutrients(mi.getFood(), mi.getQuantity());
            for (Map.Entry<String, Float> entry : nutrients.entrySet()) {
                totals.merge(entry.getKey(), entry.getValue(), Float::sum);
            }
        }
        return totals;
    }

    public void printNutrients(Map<String, Float> nutrients) {
        System.out.println("Original full meal nutrients:");
        for (Map.Entry<String, Float> entry : nutrients.entrySet()) {
            if (DISPLAY_NUTRIENT_MAP.containsKey(entry.getKey())) {
                System.out.printf("  %s (%s): %.3f\n", DISPLAY_NUTRIENT_MAP.get(entry.getKey()), entry.getKey(), entry.getValue());
            }
        }
    }
}
