package statsVisualiser.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CFGComparisonFrame extends JFrame {

    public CFGComparisonFrame(Map<String, Double> userCFGBreakdown) {
        setTitle("Canada Food Guide Comparison");
        setSize(600, 600);  // Increased height for text below
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        DefaultPieDataset dataset = new DefaultPieDataset();

        // Recommended CFG proportions (2019 guide)
        Map<String, Double> recommended = Map.of(
            "Vegetables & Fruits", 50.0,
            "Whole Grains", 25.0,
            "Protein Foods", 25.0
        );

        // Populate dataset with user breakdown values
        for (Map.Entry<String, Double> entry : userCFGBreakdown.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            "Your Average Daily Plate vs Canada Food Guide",
            dataset,
            true, true, false
        );

        // Chart panel
        ChartPanel chartPanel = new ChartPanel(pieChart);
        add(chartPanel, BorderLayout.CENTER);

        // Create HTML formatted recommendation text
        StringBuilder recText = new StringBuilder("<html><div style='font-family:sans-serif; padding:10px;'>");
        recText.append("<h3>Canada Food Guide Recommended Portions</h3><ul>");
        for (Map.Entry<String, Double> recEntry : recommended.entrySet()) {
            recText.append("<li>")
                   .append(recEntry.getKey())
                   .append(": ")
                   .append(String.format("%.0f%%", recEntry.getValue()))
                   .append("</li>");
        }
        recText.append("</ul></div></html>");

        JLabel recommendationLabel = new JLabel(recText.toString());
        recommendationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        recommendationLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(recommendationLabel, BorderLayout.SOUTH);
    }
}
