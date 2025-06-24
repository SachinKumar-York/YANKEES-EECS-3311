package statsVisualiser.gui;

import DAO.NutrientDatabase;
import Models.GoalRequest;
import Models.Meal;
import Models.MealIngredient;
import Models.NutritionalGoal;
import Models.SwapSuggestion;

import java.util.*;

public class SwapEngine {
    private final NutrientDatabase nutrientDatabase = new NutrientDatabase();

    public List<SwapSuggestion> generateSwaps(Meal meal, GoalRequest request) {
        List<NutritionalGoal> goals = request.getGoals();
        List<MealIngredient> items = meal.getItems();
        List<SwapSuggestion> suggestions = new ArrayList<>();

        System.out.println("Starting swap generation for meal with " + items.size() + " ingredients.");
        System.out.println("Goals: ");
        for (NutritionalGoal goal : goals) {
            System.out.printf(" - Nutrient: %s, Delta: %.2f, IncreaseGoal: %b%n",
                    goal.getNutrient(), goal.getDelta(), goal.isIncrease());
        }

        for (MealIngredient item : items) {
            System.out.println("\nChecking item: " + item.getFood().getFoodDescription() +
                    " (Qty: " + item.getQuantity() + "g)");

            // Get nutrients for original item scaled by quantity
            Map<String, Float> originalNutrients = nutrientDatabase.getNutrients(item.getFood(), item.getQuantity());

            System.out.println("Original nutrient values (scaled):");
            originalNutrients.forEach((k, v) -> System.out.printf("  %s: %.3f%n", k, v));

            // Find similar items to consider swapping
            List<MealIngredient> similarItems = nutrientDatabase.getSimilarItems(item);
            System.out.println("Found " + similarItems.size() + " similar items to consider.");

            for (MealIngredient altItem : similarItems) {
                System.out.println("  Considering alternative: " + altItem.getFood().getFoodDescription() +
                        " (Qty: " + altItem.getQuantity() + "g)");

                Map<String, Float> altNutrients = nutrientDatabase.getNutrients(altItem.getFood(), altItem.getQuantity());

                System.out.println("  Alternative nutrient values (scaled):");
                altNutrients.forEach((k, v) -> System.out.printf("    %s: %.3f%n", k, v));

                boolean valid = true;
                for (NutritionalGoal goal : goals) {
                    if (!goal.isGoalMet(originalNutrients, altNutrients)) {
                        System.out.printf("    Goal not met for nutrient %s%n", goal.getNutrient());
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    String justification = "Meets nutritional goals within tolerance.";
                    System.out.println("    --> Valid swap found: " + item.getFood().getFoodDescription() +
                            " → " + altItem.getFood().getFoodDescription());
                    suggestions.add(new SwapSuggestion(item.getFood(), altItem.getFood(), justification));
                } else {
                    System.out.println("    --> Swap rejected based on goals.");
                }
            }
        }

        System.out.println("Swap generation completed. Total valid suggestions: " + suggestions.size());
        return suggestions;
    }
}
