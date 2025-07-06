package Models;

import DAO.FoodDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MealService {
    private static final Logger LOGGER = Logger.getLogger(MealService.class.getName());
    private final FoodDAO foodDAO = new FoodDAO();

    public List<Meal> applySwapToAllMeals(Meal originalMeal, Meal swappedMeal, MealIngredient originalIngredient,
                                          MealIngredient swappedIngredient) {
        List<Meal> originalMeals = fetchAllMeals();
        List<Meal> swappedMeals = new ArrayList<>();
        String justification = "Swap for nutritional improvement";
        SwapSuggestion suggestion = new SwapSuggestion(originalIngredient.getFood(), swappedIngredient.getFood(), justification);

        for (Meal meal : originalMeals) {
        	Meal swapped = applySwap(meal, suggestion);
        	if (!mealToString(meal).equals(mealToString(swapped))) {
        	    swappedMeals.add(swapped);
        	}

            LOGGER.log(Level.INFO, "Swapped Meal: " + mealToString(swapped));
        }

        LOGGER.log(Level.INFO, "Total swapped meals: " + swappedMeals.size());
        return swappedMeals;
    }

    public List<Meal> applySwapToDateRange(Meal originalMeal, Meal swappedMeal, MealIngredient originalIngredient,
                                           MealIngredient swappedIngredient, LocalDate startDate, LocalDate endDate) {
        List<Meal> originalMeals = fetchMeals(startDate, endDate);
        List<Meal> swappedMeals = new ArrayList<>();
        String justification = "Swap for nutritional improvement";
        SwapSuggestion suggestion = new SwapSuggestion(originalIngredient.getFood(), swappedIngredient.getFood(), justification);

        for (Meal meal : originalMeals) {
        	Meal swapped = applySwap(meal, suggestion);
        	if (!mealToString(meal).equals(mealToString(swapped))) {
        	    swappedMeals.add(swapped);
        	}


            LOGGER.log(Level.INFO, "Swapped Meal: " + mealToString(swapped));
        }

        LOGGER.log(Level.INFO, "Total swapped meals in range " + startDate + " to " + endDate + ": " + swappedMeals.size());
        return swappedMeals;
    }

    public List<Meal> fetchAllMeals() {
        int userId = Session.getCurrentUserId();
        if (userId <= 0) {
            System.err.println("User not logged in or invalid user ID.");
            return List.of();
        }

        List<String> mealStrings = foodDAO.getLoggedMealsWithCaloriesForUser(userId);
        System.out.println("Fetched raw meal strings for user " + userId + ": " + mealStrings);

        return mealStrings.stream()
                .map(s -> {
                    try {
                        String cleaned = s.replaceAll("<[^>]+>", "")   // Remove all HTML tags
                                          .replaceAll("&#8226;", "•")   // Convert bullet entity
                                          .trim();

                        String mealIdStr = cleaned.split("•")[0].trim();
                        int mealId = Integer.parseInt(mealIdStr);

                        Meal meal = new Meal(foodDAO.getMealIngredients(mealId));
                        System.out.println("Parsed meal: " + mealToString(meal));
                        return meal;
                    } catch (Exception e) {
                        System.err.println("Failed to parse meal string: " + s);
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(meal -> meal != null && !meal.getItems().isEmpty())
                .collect(Collectors.toList());
    }

    public List<Meal> fetchMeals(LocalDate startDate, LocalDate endDate) {
        int userId = Session.getCurrentUserId();
        if (userId <= 0) {
            System.err.println("User not logged in or invalid user ID.");
            return List.of();
        }

        List<String> mealStrings = foodDAO.getLoggedMealsWithCaloriesForUser(userId);
        System.out.println("Filtering meals between " + startDate + " and " + endDate + " for user " + userId);

        return mealStrings.stream()
                .map(s -> {
                    try {
                        String cleaned = s.replaceAll("<[^>]+>", "")
                                          .replaceAll("&#8226;", "•")
                                          .trim();

                        String[] parts = cleaned.split(" ");
                        String dateStr = parts[parts.length - 3]; // Date is third from the end
                        LocalDate mealDate = LocalDate.parse(dateStr);

                        if (!mealDate.isBefore(startDate) && !mealDate.isAfter(endDate)) {
                            String mealIdStr = cleaned.split("•")[0].trim();
                            int mealId = Integer.parseInt(mealIdStr);
                            Meal meal = new Meal(foodDAO.getMealIngredients(mealId));
                            System.out.println("Included meal on " + mealDate + ": " + mealToString(meal));
                            return meal;
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to parse meal or date: " + s);
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(meal -> meal != null && !meal.getItems().isEmpty())
                .collect(Collectors.toList());
    }

    private Meal applySwap(Meal meal, SwapSuggestion suggestion) {
        System.out.println("Applying swap to meal: " + mealToString(meal));
        Meal swapped = MealUtils.buildSwappedMeal(meal, suggestion);
        System.out.println("Resulting swapped meal: " + mealToString(swapped));
        return swapped;
    }

    private String mealToString(Meal meal) {
        if (meal == null || meal.getItems() == null) return "No data";
        StringBuilder sb = new StringBuilder("Meal Ingredients: ");
        for (MealIngredient mi : meal.getItems()) {
            sb.append(mi.getFood().getFoodDescription()).append(" (").append(mi.getQuantity()).append("g), ");
        }
        return sb.toString();
    }
}
