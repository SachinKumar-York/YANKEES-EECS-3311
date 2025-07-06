package statsVisualiser.gui;

import Models.ApplySwapToAllMeals;
import Models.ApplySwapToDateRange;
import Models.Meal;
import Models.MealIngredient;
import Models.MealService;
import Models.SwapInvoker;
import Models.SwapCommand;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SwapOptionsView extends JFrame {
    private final MealService mealService;
    private final Meal originalMeal;
    private final Meal swappedMeal;
    private final MealIngredient originalIngredient;
    private final MealIngredient swappedIngredient;
    private final SwapInvoker invoker;

    public SwapOptionsView(Meal originalMeal, Meal swappedMeal, MealIngredient originalIngredient,
                          MealIngredient swappedIngredient) {
        super("Swap Options");
        this.mealService = new MealService();
        this.originalMeal = originalMeal;
        this.swappedMeal = swappedMeal;
        this.originalIngredient = originalIngredient;
        this.swappedIngredient = swappedIngredient;
        this.invoker = new SwapInvoker();

        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JButton allMealsButton = new JButton("Apply Swap to All Meals");
        JTextField startDateField = new JTextField("2025-07-01", 10);
        JTextField endDateField = new JTextField("2025-07-06", 10);
        JButton dateRangeButton = new JButton("Apply Swap to Date Range");

        allMealsButton.addActionListener(e -> {
            SwapCommand cmd = new ApplySwapToAllMeals(mealService, originalMeal, swappedMeal, originalIngredient, swappedIngredient);
            invoker.setCommand(cmd);
            invoker.run();
        });

        dateRangeButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText());
                LocalDate endDate = LocalDate.parse(endDateField.getText());
                if (startDate.isAfter(endDate)) {
                    JOptionPane.showMessageDialog(this, "Start date must be before end date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SwapCommand cmd = new ApplySwapToDateRange(mealService, originalMeal, swappedMeal, originalIngredient, swappedIngredient, startDate, endDate);
                invoker.setCommand(cmd);
                invoker.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(allMealsButton);
        add(new JLabel("Start Date:"));
        add(startDateField);
        add(new JLabel("End Date:"));
        add(endDateField);
        add(dateRangeButton);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
