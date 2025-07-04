package statsVisualiser.gui;

import javax.swing.*;
import java.awt.*;

public class AddGoalMealListFrame extends JFrame {

    public AddGoalMealListFrame(int userId) {
        setTitle("Select a Meal to Add Nutrition Goal");
        setSize(600, 550);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Select a Meal to Add Goal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ✅ Use reusable MealSelectionPanel with different double-click behavior
        MealSelectionPanel mealSelectionPanel = new MealSelectionPanel(userId, mealId -> {
            // Double click on a meal → open AddNutritionGoalFrame
            new AddNutritionGoalFrame(userId, mealId).setVisible(true);
        });

        add(mealSelectionPanel, BorderLayout.CENTER);
    }
}
