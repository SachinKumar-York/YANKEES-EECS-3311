package statsVisualiser.gui;

import Models.Session;

import javax.swing.*;
import java.awt.*;

public class ViewMealsFrame extends JFrame {

    public ViewMealsFrame(int userId) {
        setTitle("Logged Meals");
        setSize(600, 550);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(" Your Logged Meals", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ✅ Reuse MealSelectionPanel with double-click handler for pie chart
        MealSelectionPanel mealSelectionPanel = new MealSelectionPanel(userId, mealId -> {
            if (Session.isLoggedIn()) {
                int currentUserId = Session.getCurrentUserId();
                new MealPieChartFrame(currentUserId, mealId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "User session is invalid.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(mealSelectionPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int dummyUserId = 1;
            ViewMealsFrame frame = new ViewMealsFrame(dummyUserId);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
