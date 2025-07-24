package statsVisualiser.gui;

import Models.UserProfile;
import Models.Session;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private final JFrame homeRef;

    public Dashboard(JFrame home, UserProfile user) {
        this.homeRef = home;
        setTitle("NutriSci Dashboard");
        setSize(800, 500);
        setLayout(new BorderLayout(10, 10));

        // ========== Header ==========
        JLabel header = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        // ========== Sidebar (Navigation) ==========
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));
        navPanel.setPreferredSize(new Dimension(250, getHeight()));

        JButton logMealBtn = new JButton(" Log Meal ");
        JButton viewLoggedMealsBtn = new JButton(" View Logged Meals ");
        JButton addNutritionGoalBtn = new JButton(" Add Nutrition Goal ");
        JButton viewDailyIntakeBtn = new JButton(" View Daily Nutrient Intake ");
        JButton homeBtn = new JButton(" Return Home ");

        for (JButton btn : new JButton[]{logMealBtn, viewLoggedMealsBtn, addNutritionGoalBtn, viewDailyIntakeBtn, homeBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(220, 40));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setFocusable(false);
            navPanel.add(Box.createVerticalStrut(10));
            navPanel.add(btn);
        }

        add(navPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        JLabel infoLabel = new JLabel("Select an option from the navigation panel.", SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        centerPanel.add(infoLabel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        logMealBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new MealLogFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewLoggedMealsBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new ViewMealsFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addNutritionGoalBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new AddGoalMealListFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewDailyIntakeBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new DailyNutrientIntakeFrame(userId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        homeBtn.addActionListener(e -> {
            homeRef.setVisible(true);
            dispose();
        });

        homeRef.setVisible(false);
        setLocationRelativeTo(homeRef);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        JFrame homeFrame = new JFrame("Home");
        homeFrame.setSize(400, 300);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setVisible(true);

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
        Session.login(dummyUser.getUserId());

        SwingUtilities.invokeLater(() -> new Dashboard(homeFrame, dummyUser).setVisible(true));
    }
}
