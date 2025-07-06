package Models;

import DAO.NutrientDatabase;

import java.util.*;

public class MealUtils {

    private static final List<String> DEFAULT_NUTRIENT_SYMBOLS = List.of("KCAL", "PROT", "FAT", "CARB", "TDF");

    /**
     * Build a new Meal by replacing one ingredient (original food) with a suggested food
     * while keeping the quantity the same.
     */
    public static Meal buildSwappedMeal(Meal originalMeal, SwapSuggestion suggestion) {
        if (originalMeal == null || suggestion == null) return null;

        List<MealIngredient> newIngredients = new ArrayList<>();
        for (MealIngredient mi : originalMeal.getItems()) {
            if (mi.getFood().equals(suggestion.getOriginal())) {
                newIngredients.add(new MealIngredient(suggestion.getSuggested(), mi.getQuantity()));
            } else {
                newIngredients.add(mi);
            }
        }
        return new Meal(newIngredients);
    }

    /**
     * Calculate total nutrient amounts for a meal with default nutrient symbols and NutrientDatabase.
     */
    public static Map<String, Float> calculateMealNutrients(Meal meal) {
        NutrientDatabase nutrientDatabase = new NutrientDatabase();
        return calculateMealNutrients(meal, nutrientDatabase, DEFAULT_NUTRIENT_SYMBOLS);
    }

    /**
     * Calculate total nutrient amounts for a meal given nutrient symbols and NutrientDatabase.
     */
    public static Map<String, Float> calculateMealNutrients(Meal meal, NutrientDatabase nutrientDatabase, List<String> nutrientSymbols) {
        Map<String, Float> totals = new HashMap<>();
        if (meal == null || nutrientDatabase == null) return totals;

        for (MealIngredient mi : meal.getItems()) {
            Map<String, Float> nutrients = getNutrients(mi.getFood(), mi.getQuantity());
            for (String symbol : nutrientSymbols) {
                float val = nutrients.getOrDefault(symbol, 0f);
                totals.put(symbol, totals.getOrDefault(symbol, 0f) + val);
            }
        }
        return totals;
    }

    /**
     * Fetch nutrient map for a food item at a certain quantity.
     */
    public static Map<String, Float> getNutrients(Food food, double quantity) {
        NutrientDatabase nutrientDatabase = new NutrientDatabase();
        return nutrientDatabase.getNutrients(food, quantity);
    }

    /**
     * Format meal ingredients into a String array for use in JList.
     */
    public static String[] formatIngredientList(Meal meal) {
        if (meal == null || meal.getItems() == null) return new String[0];
        List<String> formatted = new ArrayList<>();
        for (MealIngredient mi : meal.getItems()) {
            formatted.add(String.format("%s (%.2f g)", mi.getFood().getFoodDescription(), mi.getQuantity()));
        }
        return formatted.toArray(new String[0]);
    }
}
