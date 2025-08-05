//SwapEngine.java start

// code before god class refactoring

//package Models;
//
//import DAO.NutrientDatabase;
//
//import java.util.*;
//
//public class SwapEngine {
//    private final NutrientDatabase nutrientDatabase = new NutrientDatabase();
//
//    private static final Map<String, String> DISPLAY_NUTRIENT_MAP = Map.of(
//        "TDF", "Fibre",
//        "KCAL", "Calories",
//        "FAT", "Fat",
//        "PROT", "Protein",
//        "CARB", "Carbohydrate"
//    );
//
//    // code before refactoring
//    
////    public List<SwapSuggestion> generateSwaps(Meal meal, GoalRequest request) {
////        GoalComponent goalComponent = request.getGoalComponent();
////        Set<String> goalNutrients = goalComponent.getTargetNutrients();
////
////        List<MealIngredient> items = meal.getItems();
////        List<SwapSuggestion> suggestions = new ArrayList<>();
////
////        System.out.println("Starting swap generation for meal with " + items.size() + " ingredients.");
////
////        Map<String, Float> originalMealTotals = computeMealNutrients(items);
////        System.out.println("Original full meal nutrients:");
////        printNutrients(originalMealTotals);
////
////        for (MealIngredient item : items) {
////            System.out.println("\nChecking item: " + item.getFood().getFoodDescription() +
////                    " (Qty: " + item.getQuantity() + "g)");
////
////            List<MealIngredient> similarItems = nutrientDatabase.getSimilarItems(item);
////            System.out.println("Found " + similarItems.size() + " similar items to consider.");
////
////            for (MealIngredient altItem : similarItems) {
////                System.out.println("  Considering alternative: " + altItem.getFood().getFoodDescription() +
////                        " (Qty: " + altItem.getQuantity() + "g)");
////
////                // Build hypothetical swapped meal
////                List<MealIngredient> modifiedMeal = new ArrayList<>();
////                for (MealIngredient original : items) {
////                    if (original == item) {
////                        modifiedMeal.add(altItem);  // Swap in alternative
////                    } else {
////                        modifiedMeal.add(original);
////                    }
////                }
////
////                Map<String, Float> newMealTotals = computeMealNutrients(modifiedMeal);
////
////                boolean withinLimits = true;
////                for (String symbol : DISPLAY_NUTRIENT_MAP.keySet()) {
////                    if (goalNutrients.contains(symbol)) continue;
////
////                    float original = originalMealTotals.getOrDefault(symbol, 0f);
////                    float alt = newMealTotals.getOrDefault(symbol, 0f);
////
////                    if (original == 0f && alt == 0f) continue;
////                    if (original == 0f) {
////                        withinLimits = false;
////                        break;
////                    }
////
////                    float percentDiff = Math.abs(alt - original) / original;
////                    if (percentDiff > 0.10f) {
////                        System.out.printf("    --> Rejected (whole meal): %s exceeds 10%% (%.2f%%)\n", symbol, percentDiff * 100);
////                        withinLimits = false;
////                        break;
////                    }
////                }
////
////                if (withinLimits && goalComponent.isGoalMet(originalMealTotals, newMealTotals)) {
////                    String justification = "Swapping keeps full meal nutrients within 10% and meets goal.";
////                    System.out.println("    --> Valid meal-level swap: " + item.getFood().getFoodDescription() +
////                            " → " + altItem.getFood().getFoodDescription());
////                    suggestions.add(new SwapSuggestion(item.getFood(), altItem.getFood(), justification));
////                } else if (!withinLimits) {
////                    System.out.println("    --> Swap rejected due to total meal deviation.");
////                } else {
////                    System.out.println("    --> Swap rejected based on goal evaluation.");
////                }
////            }
////        }
////
////        System.out.println("Swap generation completed. Total valid suggestions: " + suggestions.size());
////        return suggestions;
////    }
//    
//  //----------------------------------------------
//    
//    public List<SwapSuggestion> generateSwaps(Meal meal, GoalRequest request) {
//        GoalComponent goalComponent = request.getGoalComponent();
//        Set<String> goalNutrients = goalComponent.getTargetNutrients();
//        List<MealIngredient> items = meal.getItems();
//
//        System.out.println("Starting swap generation for meal with " + items.size() + " ingredients.");
//        Map<String, Float> originalMealTotals = computeMealNutrients(items);
//        printOriginalNutrients(originalMealTotals);
//
//        List<SwapSuggestion> suggestions = new ArrayList<>();
//
//        for (MealIngredient item : items) {
//            logCheckingItem(item);
//            List<MealIngredient> similarItems = nutrientDatabase.getSimilarItems(item);
//            System.out.println("Found " + similarItems.size() + " similar items to consider.");
//
//            for (MealIngredient altItem : similarItems) {
//                logConsideringAlternative(altItem);
//                List<MealIngredient> modifiedMeal = buildModifiedMeal(items, item, altItem);
//                Map<String, Float> newMealTotals = computeMealNutrients(modifiedMeal);
//
//                if (isWithinAcceptableDeviation(originalMealTotals, newMealTotals, goalNutrients)
//                        && goalComponent.isGoalMet(originalMealTotals, newMealTotals)) {
//                    logValidSwap(item, altItem);
//                    suggestions.add(new SwapSuggestion(item.getFood(), altItem.getFood(),
//                            "Swapping keeps full meal nutrients within 10% and meets goal."));
//                } else {
//                    logRejectedSwap(originalMealTotals, newMealTotals, goalNutrients);
//                }
//            }
//        }
//
//        System.out.println("Swap generation completed. Total valid suggestions: " + suggestions.size());
//        return suggestions;
//    }
//
//    private void printOriginalNutrients(Map<String, Float> nutrients) {
//        System.out.println("Original full meal nutrients:");
//        printNutrients(nutrients);
//    }
//
//    private void logCheckingItem(MealIngredient item) {
//        System.out.println("\nChecking item: " + item.getFood().getFoodDescription() +
//                " (Qty: " + item.getQuantity() + "g)");
//    }
//
//    private void logConsideringAlternative(MealIngredient altItem) {
//        System.out.println("  Considering alternative: " + altItem.getFood().getFoodDescription() +
//                " (Qty: " + altItem.getQuantity() + "g)");
//    }
//
//    private void logValidSwap(MealIngredient original, MealIngredient alternative) {
//        System.out.println("    --> Valid meal-level swap: " +
//                original.getFood().getFoodDescription() + " → " + alternative.getFood().getFoodDescription());
//    }
//
//    private void logRejectedSwap(Map<String, Float> original, Map<String, Float> alt, Set<String> goalNutrients) {
//        for (String symbol : DISPLAY_NUTRIENT_MAP.keySet()) {
//            if (goalNutrients.contains(symbol)) continue;
//            float origVal = original.getOrDefault(symbol, 0f);
//            float altVal = alt.getOrDefault(symbol, 0f);
//            if (origVal == 0f && altVal == 0f) continue;
//            if (origVal == 0f || Math.abs(altVal - origVal) / origVal > 0.10f) {
//                System.out.printf("    --> Rejected (whole meal): %s exceeds 10%% (%.2f%%)\n",
//                        symbol, (origVal == 0f ? 100.0 : Math.abs(altVal - origVal) / origVal * 100));
//                System.out.println("    --> Swap rejected due to total meal deviation.");
//                return;
//            }
//        }
//        System.out.println("    --> Swap rejected based on goal evaluation.");
//    }
//
//    private List<MealIngredient> buildModifiedMeal(List<MealIngredient> originalItems,
//                                                   MealIngredient toReplace, MealIngredient alternative) {
//        List<MealIngredient> modified = new ArrayList<>();
//        for (MealIngredient item : originalItems) {
//            modified.add(item == toReplace ? alternative : item);
//        }
//        return modified;
//    }
//
//    private boolean isWithinAcceptableDeviation(Map<String, Float> original,
//                                                Map<String, Float> alternative,
//                                                Set<String> goalNutrients) {
//        for (String symbol : DISPLAY_NUTRIENT_MAP.keySet()) {
//            if (goalNutrients.contains(symbol)) continue;
//
//            float orig = original.getOrDefault(symbol, 0f);
//            float alt = alternative.getOrDefault(symbol, 0f);
//
//            if (orig == 0f && alt == 0f) continue;
//            if (orig == 0f || Math.abs(alt - orig) / orig > 0.10f) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    //----------------------------------------------
//    
//
//    private Map<String, Float> computeMealNutrients(List<MealIngredient> ingredients) {
//        Map<String, Float> totals = new HashMap<>();
//
//        for (MealIngredient mi : ingredients) {
//            Map<String, Float> nutrients = nutrientDatabase.getNutrients(mi.getFood(), mi.getQuantity());
//            for (Map.Entry<String, Float> entry : nutrients.entrySet()) {
//                totals.merge(entry.getKey(), entry.getValue(), Float::sum);
//            }
//        }
//
//        return totals;
//    }
//
//    private void printNutrients(Map<String, Float> nutrients) {
//        for (Map.Entry<String, Float> entry : nutrients.entrySet()) {
//            if (DISPLAY_NUTRIENT_MAP.containsKey(entry.getKey())) {
//                System.out.printf("  %s (%s): %.3f\n", DISPLAY_NUTRIENT_MAP.get(entry.getKey()), entry.getKey(), entry.getValue());
//            }
//        }
//    }
//}


//---------------------------------------

package Models;

import DAO.NutrientDatabase;
import java.util.*;

public class SwapEngine {
    private final NutrientDatabase nutrientDatabase = new NutrientDatabase();
    private final NutrientComparator nutrientComparator = new NutrientComparator();
    private final SwapValidator swapValidator = new SwapValidator();

    public List<SwapSuggestion> generateSwaps(Meal meal, GoalRequest request) {
        GoalComponent goalComponent = request.getGoalComponent();
        Set<String> goalNutrients = goalComponent.getTargetNutrients();
        List<MealIngredient> items = meal.getItems();

        System.out.println("Starting swap generation for meal with " + items.size() + " ingredients.");
        Map<String, Float> originalMealTotals = nutrientComparator.computeMealNutrients(items);
        nutrientComparator.printNutrients(originalMealTotals);

        List<SwapSuggestion> suggestions = new ArrayList<>();

        for (MealIngredient item : items) {
            System.out.println("\nChecking item: " + item.getFood().getFoodDescription() +
                    " (Qty: " + item.getQuantity() + "g)");

            List<MealIngredient> similarItems = nutrientDatabase.getSimilarItems(item);
            System.out.println("Found " + similarItems.size() + " similar items to consider.");

            for (MealIngredient altItem : similarItems) {
                System.out.println("  Considering alternative: " + altItem.getFood().getFoodDescription() +
                        " (Qty: " + altItem.getQuantity() + "g)");

                List<MealIngredient> modifiedMeal = buildModifiedMeal(items, item, altItem);
                Map<String, Float> newMealTotals = nutrientComparator.computeMealNutrients(modifiedMeal);

                if (swapValidator.isSwapValid(originalMealTotals, newMealTotals, goalNutrients, goalComponent)) {
                    suggestions.add(new SwapSuggestion(item.getFood(), altItem.getFood(),
                            "Swapping keeps full meal nutrients within 10% and meets goal."));
                    System.out.println("    --> Valid meal-level swap: " + item.getFood().getFoodDescription() +
                            " → " + altItem.getFood().getFoodDescription());
                } else {
                    System.out.println("    --> Swap rejected.");
                }
            }
        }

        System.out.println("Swap generation completed. Total valid suggestions: " + suggestions.size());
        return suggestions;
    }

    private List<MealIngredient> buildModifiedMeal(List<MealIngredient> originalItems,
                                                   MealIngredient toReplace, MealIngredient alternative) {
        List<MealIngredient> modified = new ArrayList<>();
        for (MealIngredient item : originalItems) {
            modified.add(item == toReplace ? alternative : item);
        }
        return modified;
    }
}


//---------------------------------------
