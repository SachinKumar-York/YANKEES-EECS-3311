package statsVisualiser.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RecommendedComparisonFrame extends JFrame {

    private static final Map<String, Double> recommended = Map.of(
        "PROT", 50.0,
        "CARB", 275.0,
        "FAT", 70.0,
        "FIBR", 30.0,
        "KCAL", 2000.0
    );

    public RecommendedComparisonFrame(Map<String, Double> userAvg) {
        setTitle("Comparison with Recommended Daily Portions");
        setSize(700, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : recommended.entrySet()) {
            String nutrient = entry.getKey();
            double userValue = userAvg.getOrDefault(nutrient, 0.0);
            double recommendedValue = entry.getValue();

            dataset.addValue(userValue, "Your Avg Intake", nutrient);
            dataset.addValue(recommendedValue, "Recommended", nutrient);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Daily Intake vs Recommended",
                "Nutrient",
                "Amount (g or kcal)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        add(chartPanel, BorderLayout.CENTER);
    }
}
