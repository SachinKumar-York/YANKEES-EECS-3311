package Models;

import javax.swing.*;
import java.util.List;

public class ApplySwapToAllMeals implements SwapCommand {
    private final MealService mealService;
    private final Meal originalMeal;
    private final Meal swappedMeal;
    private final MealIngredient originalIngredient;
    private final MealIngredient swappedIngredient;

    public ApplySwapToAllMeals(MealService service, Meal originalMeal, Meal swappedMeal,
                               MealIngredient originalIngredient, MealIngredient swappedIngredient) {
        this.mealService = service;
        this.originalMeal = originalMeal;
        this.swappedMeal = swappedMeal;
        this.originalIngredient = originalIngredient;
        this.swappedIngredient = swappedIngredient;
    }

    @Override
    public void execute() {
        System.out.println("Executing swap for ALL meals...");
        List<Meal> swappedMeals = mealService.applySwapToAllMeals(originalMeal, swappedMeal, originalIngredient, swappedIngredient);
        System.out.println("Final swapped meal count: " + swappedMeals.size());

        int result = JOptionPane.showConfirmDialog(null,
                "Swapped " + swappedMeals.size() + " meals containing the ingredient.\nWant to visualize changes?",
                "Success", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> new statsVisualiser.gui.SwapVisualizationView(originalMeal, swappedMeals).setVisible(true));
        }
    }
}
