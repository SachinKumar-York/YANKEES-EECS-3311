package statsVisualiser.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class DailyNutrientIntakeFrame extends JFrame {
    private final int userId;
    private final JSpinner startDateSpinner;
    private final JSpinner endDateSpinner;

    public DailyNutrientIntakeFrame(int userId) {
        this.userId = userId;
        setTitle("Select Date Range");
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use a vertical BoxLayout so components stack nicely
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        // Start date
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel startLabel = new JLabel("Start Date:");
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startPanel.add(startLabel);
        startPanel.add(startDateSpinner);
        contentPanel.add(startPanel);

        // End date
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel endLabel = new JLabel("End Date:");
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        endPanel.add(endLabel);
        endPanel.add(endDateSpinner);
        contentPanel.add(endPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton proceedBtn = new JButton("Get Meals Within Date Range");
        proceedBtn.addActionListener(e -> openNutrientPieChart());
        buttonPanel.add(proceedBtn);
        contentPanel.add(buttonPanel);
    }

    private void openNutrientPieChart() {
        Date start = (Date) startDateSpinner.getValue();
        Date end = (Date) endDateSpinner.getValue();

        if (start.after(end)) {
            JOptionPane.showMessageDialog(this,
                "Start date cannot be after end date.",
                "Invalid Date Range",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        new NutrientPieChartFrame(userId, start, end).setVisible(true);
        this.dispose();  // optionally close this window after opening chart
    }
}
