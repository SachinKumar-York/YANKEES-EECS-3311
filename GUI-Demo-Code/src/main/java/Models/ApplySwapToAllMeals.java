package Models;

import DAO.FoodDAO;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ApplySwapToAllMeals implements SwapCommand {
    private final MealService mealService;
    private final Meal originalMeal;
    private final Meal swappedMeal;
    private final MealIngredient originalIngredient;
    private final MealIngredient swappedIngredient;
    private final int userId;
    private final FoodDAO foodDAO;

    public ApplySwapToAllMeals(MealService service, Meal originalMeal, Meal swappedMeal,
                               MealIngredient originalIngredient, MealIngredient swappedIngredient, int userId) {
        this.mealService = service;
        this.originalMeal = originalMeal;
        this.swappedMeal = swappedMeal;
        this.originalIngredient = originalIngredient;
        this.swappedIngredient = swappedIngredient;
        this.userId = userId;
        this.foodDAO = new FoodDAO();
    }

    @Override
    public void execute() {
        System.out.println("Executing swap for ALL meals...");
        List<Meal> swappedMeals = mealService.applySwapToAllMeals(originalMeal, swappedMeal, originalIngredient, swappedIngredient);
        List<Meal> originalMeals = mealService.fetchAllMeals().stream()
                .filter(meal -> meal.getItems().stream()
                        .anyMatch(i -> i.getFood().getFoodId() == originalIngredient.getFood().getFoodId()))
                .collect(Collectors.toList());
        List<Integer> mealIds = getMealIdsForMeals(originalMeals);
        System.out.println("Final swapped meal count: " + swappedMeals.size());
        System.out.println("Original meals count: " + originalMeals.size() + ", Meal IDs: " + mealIds);

        int result = JOptionPane.showConfirmDialog(null,
                "Swapped " + swappedMeals.size() + " meals containing the ingredient.\nWant to visualize changes?",
                "Success", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (originalMeals.isEmpty() || swappedMeals.isEmpty() || mealIds.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No meals available to visualize.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            SwingUtilities.invokeLater(() -> new statsVisualiser.gui.SwapVisualizationView(
                    originalMeals, swappedMeals, mealIds, userId).setVisible(true));
        }
    }

    private List<Integer> getMealIdsForMeals(List<Meal> meals) {
        List<Integer> mealIds = new ArrayList<>();
        List<String> mealStrings = foodDAO.getLoggedMealsWithCaloriesForUser(userId);

        for (Meal meal : meals) {
            int mealId = -1;
            for (String s : mealStrings) {
                try {
                    // Extract MealID from <span style='display:none'>NN</span> using regex
                    Pattern pattern = Pattern.compile("<span style='display:none'>([0-9]+)</span>");
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        String mealIdStr = matcher.group(1);
                        System.out.println("Extracted mealIdStr from HTML: " + mealIdStr);
                        int currentMealId = Integer.parseInt(mealIdStr);
                        List<MealIngredient> ingredients = foodDAO.getMealIngredients(currentMealId);
                        Meal tempMeal = new Meal(ingredients);
                        if (mealToString(tempMeal).equals(mealToString(meal))) {
                            mealId = currentMealId;
                            break;
                        }
                    } else {
                        System.err.println("No MealID found in HTML string: " + s);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse meal ID from string: " + s);
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Error processing meal string: " + s);
                    e.printStackTrace();
                }
            }
            if (mealId != -1) {
                mealIds.add(mealId);
            } else {
                System.err.println("No MealID found for meal: " + mealToString(meal));
            }
        }
        return mealIds;
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