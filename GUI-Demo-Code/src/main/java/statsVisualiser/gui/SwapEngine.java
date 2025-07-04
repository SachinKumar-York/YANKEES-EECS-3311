package statsVisualiser.gui;

import DAO.NutrientDatabase;
import Models.*;

import java.util.*;

public class SwapEngine {
    private final NutrientDatabase nutrientDatabase = new NutrientDatabase();

    public List<SwapSuggestion> generateSwaps(Meal meal, GoalRequest request) {
        GoalComponent goalComponent = request.getGoalComponent();  
        List<MealIngredient> items = meal.getItems();
        List<SwapSuggestion> suggestions = new ArrayList<>();

        System.out.println("Starting swap generation for meal with " + items.size() + " ingredients.");

        for (MealIngredient item : items) {
            System.out.println("\nChecking item: " + item.getFood().getFoodDescription() +
                    " (Qty: " + item.getQuantity() + "g)");

            // Original nutrient values
            Map<String, Float> originalNutrients = nutrientDatabase.getNutrients(item.getFood(), item.getQuantity());
            System.out.println("Original nutrient values (scaled):");
            originalNutrients.forEach((k, v) -> System.out.printf("  %s: %.3f%n", k, v));

            // Get similar alternatives
            List<MealIngredient> similarItems = nutrientDatabase.getSimilarItems(item);
            System.out.println("Found " + similarItems.size() + " similar items to consider.");

            for (MealIngredient altItem : similarItems) {
                System.out.println("  Considering alternative: " + altItem.getFood().getFoodDescription() +
                        " (Qty: " + altItem.getQuantity() + "g)");

                Map<String, Float> altNutrients = nutrientDatabase.getNutrients(altItem.getFood(), altItem.getQuantity());

                System.out.println("  Alternative nutrient values (scaled):");
                altNutrients.forEach((k, v) -> System.out.printf("    %s: %.3f%n", k, v));

                // Use Composite Goal evaluation
                if (goalComponent.isGoalMet(originalNutrients, altNutrients)) {
                    String justification = "Meets nutritional goals within tolerance.";
                    System.out.println("    --> Valid swap found: " + item.getFood().getFoodDescription() +
                            " → " + altItem.getFood().getFoodDescription());
                    suggestions.add(new SwapSuggestion(item.getFood(), altItem.getFood(), justification));
                } else {
                    System.out.println("    --> Swap rejected based on goal evaluation.");
                }
            }
        }

        System.out.println("Swap generation completed. Total valid suggestions: " + suggestions.size());
        return suggestions;
    }
}
