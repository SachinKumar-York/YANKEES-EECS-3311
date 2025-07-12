package statsVisualiser.gui;

import DAO.FoodDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NutrientPieChartFrame extends JFrame {
    private final int userId;
    private final Date startDate;
    private final Date endDate;
    private final Map<String, Double> rawDailyAverages;

    // ✅ Key nutrients to show in pie
    private static final Set<String> KEY_NUTRIENTS = new HashSet<>(Arrays.asList(
        "PROTEIN", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "FAT (TOTAL LIPIDS)", "FIBRE, TOTAL DIETARY", "ENERGY (KILOCALORIES)", "SUCROSE"
    ));

    public NutrientPieChartFrame(int userId, Date startDate, Date endDate) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;

        setTitle("Average Daily Nutrient Intake");
        setSize(700, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        FoodDAO dao = new FoodDAO();
        this.rawDailyAverages = dao.getAverageDailyNutrientIntake(userId, startDate, endDate);

        JPanel centerPanel = new JPanel(new BorderLayout());

        // Top: Pie chart
        JPanel chartPanel = createChartPanel();
        centerPanel.add(chartPanel, BorderLayout.CENTER);

        // Bottom: Other nutrient list
        JPanel otherNutrientsPanel = createOtherNutrientsList();
        centerPanel.add(otherNutrientsPanel, BorderLayout.SOUTH);

        // South: Comparison button
        JButton compareBtn = new JButton("Compare with Recommended Daily Portions");
        compareBtn.addActionListener(e -> new RecommendedComparisonFrame(userId, rawDailyAverages).setVisible(true));

        add(centerPanel, BorderLayout.CENTER);
        add(compareBtn, BorderLayout.SOUTH);
    }

    private JPanel createChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map.Entry<String, Double> entry : rawDailyAverages.entrySet()) {
            if (KEY_NUTRIENTS.contains(entry.getKey())) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            "Average Daily Nutrient Intake (" + formatDate(startDate) + " to " + formatDate(endDate) + ")",
            dataset,
            true,
            true,
            false
        );

        return new ChartPanel(pieChart);
    }

    private JPanel createOtherNutrientsList() {
        DefaultListModel<String> model = new DefaultListModel<>();

        for (Map.Entry<String, Double> entry : rawDailyAverages.entrySet()) {
            if (!KEY_NUTRIENTS.contains(entry.getKey())) {
                model.addElement(entry.getKey() + " : " + String.format("%.2f", entry.getValue()));
            }
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(600, 120));

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Other Nutrients:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return panel;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
