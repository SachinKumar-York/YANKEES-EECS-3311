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

       
        Color backgroundColor = new Color(245, 245, 245);
        Color panelColor = new Color(255, 255, 255);
        Color headerColor = new Color(70, 130, 180);
        Color fontColor = Color.BLACK;

        getContentPane().setBackground(backgroundColor);

        
        JLabel titleLabel = new JLabel(" Your Logged Meals", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(fontColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(headerColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.setBackground(panelColor);

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

        mealSelectionPanel.setBackground(panelColor);
        centerPanel.add(mealSelectionPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
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
