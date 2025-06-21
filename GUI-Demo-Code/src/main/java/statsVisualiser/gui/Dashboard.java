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

        /* --- TOP-RIGHT: User and Return Home --- */
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton userBtn = new JButton(user.getName());
        JButton homeBtn = new JButton("Return Home");

        topBar.add(userBtn);
        topBar.add(homeBtn);
        add(topBar, BorderLayout.NORTH);

        /* --- CENTER: Log Meal button and View Logged Meals button --- */
        JPanel centerPanel = new JPanel();

        JButton logMealBtn = new JButton("Log Meal");
        logMealBtn.setPreferredSize(new Dimension(120, 40));
        logMealBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new MealLogFrame(userId).setVisible(true);  // 🚀 open MealLog window
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton viewLoggedMealsBtn = new JButton("View Logged Meals");
        viewLoggedMealsBtn.setPreferredSize(new Dimension(150, 40));
        viewLoggedMealsBtn.addActionListener(e -> {
            int userId = Session.getCurrentUserId();
            if (userId > 0) {
                new ViewMealsFrame(userId).setVisible(true);  // ✅ Opens meal viewer
            } else {
                JOptionPane.showMessageDialog(this, "User session invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        centerPanel.add(logMealBtn);
        centerPanel.add(viewLoggedMealsBtn);
        add(centerPanel, BorderLayout.CENTER);

        /* --- Return to home --- */
        homeBtn.addActionListener(e -> {
            homeRef.setVisible(true);
            dispose();
        });

        homeRef.setVisible(false);
        setLocationRelativeTo(homeRef);
    }
}