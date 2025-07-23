package statsVisualiser.gui;

import Models.UserProfile;
import Models.Session;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private final JFrame homeRef;

    public Dashboard(JFrame home, UserProfile user) {
        this.homeRef = home;
        setTitle("Dashboard - " + user.getName());
        setSize(600, 400);
        setLayout(new BorderLayout());

        // TOP-RIGHT: User and Return Home 
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton userBtn = new JButton(user.getName());
        JButton homeBtn = new JButton("Return Home");

        topBar.add(userBtn);
        topBar.add(homeBtn);
        add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();

        JButton logMealBtn = new JButton("Log Meal");
        logMealBtn.setPreferredSize(new Dimension(120, 40));
        logMealBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new MealLogFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton viewLoggedMealsBtn = new JButton("View Logged Meals");
        viewLoggedMealsBtn.setPreferredSize(new Dimension(150, 40));
        viewLoggedMealsBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new ViewMealsFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // New button: Add Nutrition Goal
        JButton addNutritionGoalBtn = new JButton("Add Nutrition Goal");
        addNutritionGoalBtn.setPreferredSize(new Dimension(160, 40));
        addNutritionGoalBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new AddGoalMealListFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton viewDailyIntakeBtn = new JButton("View Daily Nutrient Intake");
        viewDailyIntakeBtn.setPreferredSize(new Dimension(200, 40));
        viewDailyIntakeBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new DailyNutrientIntakeFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        centerPanel.add(logMealBtn);
        centerPanel.add(viewLoggedMealsBtn);
        centerPanel.add(addNutritionGoalBtn);
        centerPanel.add(viewDailyIntakeBtn);  // ✅ added here

        add(centerPanel, BorderLayout.CENTER);

        homeBtn.addActionListener(e -> {
            homeRef.setVisible(true);
            dispose();
        });

        homeRef.setVisible(false);
        setLocationRelativeTo(homeRef);
    }

    public static void main(String[] args) {
        JFrame homeFrame = new JFrame("Home");
        homeFrame.setSize(400, 300);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);

        // Create a dummy user profile (normally retrieved from DB)
        UserProfile dummyUser = new UserProfile(
            "John Doe",
            "john@example.com",
            "Male",
            new java.util.Date(),
            1.75f,
            70f,
            "Metric"
        );
        dummyUser.setUserId(1);

        // Simulate user session login
        Session.login(dummyUser.getUserId());

        // Launch the Dashboard
        SwingUtilities.invokeLater(() -> {
            new Dashboard(homeFrame, dummyUser).setVisible(true);
        });
    }
}
