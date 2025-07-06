package Models;

import DAO.NutrientDatabase;

import java.util.*;

public class SwapEngine {
    private final NutrientDatabase nutrientDatabase = new NutrientDatabase();

    private static final Map<String, String> DISPLAY_NUTRIENT_MAP = Map.of(
        "TDF", "Fibre",
        "KCAL", "Calories",
        "FAT", "Fat",
        "PROT", "Protein",
        "CARB", "Carbohydrate"
    );

    public List<SwapSuggestion> generateSwaps(Meal meal, GoalRequest request) {
        GoalComponent goalComponent = request.getGoalComponent();
        Set<String> goalNutrients = goalComponent.getTargetNutrients();

        List<MealIngredient> items = meal.getItems();
        List<SwapSuggestion> suggestions = new ArrayList<>();

        System.out.println("Starting swap generation for meal with " + items.size() + " ingredients.");

        // ✅ Step 1: Calculate original total meal nutrients
        Map<String, Float> originalMealTotals = computeMealNutrients(items);
        System.out.println("Original full meal nutrients:");
        printNutrients(originalMealTotals);

        for (MealIngredient item : items) {
            System.out.println("\nChecking item: " + item.getFood().getFoodDescription() +
                    " (Qty: " + item.getQuantity() + "g)");

            List<MealIngredient> similarItems = nutrientDatabase.getSimilarItems(item);
            System.out.println("Found " + similarItems.size() + " similar items to consider.");

            for (MealIngredient altItem : similarItems) {
                System.out.println("  Considering alternative: " + altItem.getFood().getFoodDescription() +
                        " (Qty: " + altItem.getQuantity() + "g)");

                // Build hypothetical swapped meal
                List<MealIngredient> modifiedMeal = new ArrayList<>();
                for (MealIngredient original : items) {
                    if (original == item) {
                        modifiedMeal.add(altItem);  // Swap in alternative
                    } else {
                        modifiedMeal.add(original);
                    }
                }

                // ✅ Step 2: Compute new total nutrients for swapped meal
                Map<String, Float> newMealTotals = computeMealNutrients(modifiedMeal);

                // ✅ Step 3: Check non-goal nutrient deviation from original totals
                boolean withinLimits = true;
                for (String symbol : DISPLAY_NUTRIENT_MAP.keySet()) {
                    if (goalNutrients.contains(symbol)) continue;

                    float original = originalMealTotals.getOrDefault(symbol, 0f);
                    float alt = newMealTotals.getOrDefault(symbol, 0f);

                    if (original == 0f && alt == 0f) continue;
                    if (original == 0f) {
                        withinLimits = false;
                        break;
                    }

                    float percentDiff = Math.abs(alt - original) / original;
                    if (percentDiff > 0.10f) {
                        System.out.printf("    --> Rejected (whole meal): %s exceeds 10%% (%.2f%%)\n", symbol, percentDiff * 100);
                        withinLimits = false;
                        break;
                    }
                }

                // ✅ Step 4: Check if swap improves or maintains goal nutrient values
                if (withinLimits && goalComponent.isGoalMet(originalMealTotals, newMealTotals)) {
                    String justification = "Swapping keeps full meal nutrients within 10% and meets goal.";
                    System.out.println("    --> Valid meal-level swap: " + item.getFood().getFoodDescription() +
                            " → " + altItem.getFood().getFoodDescription());
                    suggestions.add(new SwapSuggestion(item.getFood(), altItem.getFood(), justification));
                } else if (!withinLimits) {
                    System.out.println("    --> Swap rejected due to total meal deviation.");
                } else {
                    System.out.println("    --> Swap rejected based on goal evaluation.");
                }
            }
        }

        System.out.println("Swap generation completed. Total valid suggestions: " + suggestions.size());
        return suggestions;
    }

    // ✅ Utility: Sum total nutrients for a list of ingredients
    private Map<String, Float> computeMealNutrients(List<MealIngredient> ingredients) {
        Map<String, Float> totals = new HashMap<>();

        for (MealIngredient mi : ingredients) {
            Map<String, Float> nutrients = nutrientDatabase.getNutrients(mi.getFood(), mi.getQuantity());
            for (Map.Entry<String, Float> entry : nutrients.entrySet()) {
                totals.merge(entry.getKey(), entry.getValue(), Float::sum);
            }
        }

        return totals;
    }

    // ✅ Utility: Print nutrient map with readable names
    private void printNutrients(Map<String, Float> nutrients) {
        for (Map.Entry<String, Float> entry : nutrients.entrySet()) {
            if (DISPLAY_NUTRIENT_MAP.containsKey(entry.getKey())) {
                System.out.printf("  %s (%s): %.3f\n", DISPLAY_NUTRIENT_MAP.get(entry.getKey()), entry.getKey(), entry.getValue());
            }
        }
    }
}
