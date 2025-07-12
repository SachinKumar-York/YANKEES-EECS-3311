package statsVisualiser.gui;

import DAO.FoodDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class NutrientPieChartFrame extends JFrame {
    private final int userId;
    private final Date startDate;
    private final Date endDate;
    private final Map<String, Double> rawDailyAverages;

    // ✅ The 6 nutrients to show individually
    private static final Set<String> KEY_NUTRIENTS = new HashSet<>(Arrays.asList(
            "PROT", "CARB", "FAT", "FIBR", "KCAL", "SUCR"
    ));

    public NutrientPieChartFrame(int userId, Date startDate, Date endDate) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;

        setTitle("Average Daily Nutrient Intake");
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        FoodDAO dao = new FoodDAO();
        this.rawDailyAverages = dao.getAverageDailyNutrientIntake(userId, startDate, endDate);

        JPanel chartPanel = createChartPanel();
        JButton compareBtn = new JButton("Compare with Recommended Daily Portions");
        compareBtn.addActionListener(e -> new RecommendedComparisonFrame(rawDailyAverages).setVisible(true));

        add(chartPanel, BorderLayout.CENTER);
        add(compareBtn, BorderLayout.SOUTH);
    }

    private JPanel createChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        double otherSum = 0.0;

        for (Map.Entry<String, Double> entry : rawDailyAverages.entrySet()) {
            String nutrient = entry.getKey();
            double avg = entry.getValue();

            if (KEY_NUTRIENTS.contains(nutrient)) {
                dataset.setValue(nutrient, avg);
            } else {
                otherSum += avg;
            }
        }

        // ✅ Include "Other Nutrients" in chart
        if (otherSum > 0.0) {
            dataset.setValue("Other Nutrients", otherSum);
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

    private String formatDate(Date date) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
