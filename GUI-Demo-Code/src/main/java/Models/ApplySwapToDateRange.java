package Models;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class ApplySwapToDateRange implements SwapCommand {
    private final MealService mealService;
    private final Meal originalMeal;
    private final Meal swappedMeal;
    private final MealIngredient originalIngredient;
    private final MealIngredient swappedIngredient;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public ApplySwapToDateRange(MealService service, Meal originalMeal, Meal swappedMeal,
                                MealIngredient originalIngredient, MealIngredient swappedIngredient,
                                LocalDate startDate, LocalDate endDate) {
        this.mealService = service;
        this.originalMeal = originalMeal;
        this.swappedMeal = swappedMeal;
        this.originalIngredient = originalIngredient;
        this.swappedIngredient = swappedIngredient;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void execute() {
        System.out.println("Executing swap for meals between " + startDate + " and " + endDate);
        List<Meal> swappedMeals = mealService.applySwapToDateRange(originalMeal, swappedMeal, originalIngredient, swappedIngredient, startDate, endDate);
        System.out.println("Final swapped meal count: " + swappedMeals.size());

        int result = JOptionPane.showConfirmDialog(null,
                "Swapped " + swappedMeals.size() + " meals containing the ingredient from " + startDate + " to " + endDate + ".\nWant to visualize changes?",
                "Success", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> new statsVisualiser.gui.SwapVisualizationView(originalMeal, swappedMeals).setVisible(true));
        }
    }
}
