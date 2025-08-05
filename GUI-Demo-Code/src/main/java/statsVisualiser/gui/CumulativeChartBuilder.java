//CumulativeChartBuilder.java start

package statsVisualiser.gui;

import Models.Meal;
import Models.MealUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CumulativeChartBuilder {
    public static JPanel createCumulativePanel(List<Meal> originalMeals, List<Meal> swappedMeals) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JRadioButton barBtn = new JRadioButton("Bar Chart", true);
        JRadioButton lineBtn = new JRadioButton("Line Chart");
        ButtonGroup group = new ButtonGroup();
        group.add(barBtn);
        group.add(lineBtn);

        JCheckBox kcalBox = new JCheckBox("Calories", true);
        JCheckBox protBox = new JCheckBox("Protein", true);
        JCheckBox fatBox = new JCheckBox("Fat", true);
        JCheckBox carbBox = new JCheckBox("Carbs", true);
        JCheckBox tdfBox = new JCheckBox("Fibre", true);
        List<JCheckBox> boxes = List.of(kcalBox, protBox, fatBox, carbBox, tdfBox);

        controlPanel.add(barBtn);
        controlPanel.add(lineBtn);
        boxes.forEach(controlPanel::add);

        panel.add(controlPanel, BorderLayout.NORTH);
        JPanel chartHolder = new JPanel(new BorderLayout());
        panel.add(chartHolder, BorderLayout.CENTER);

        Runnable updateChart = () -> {
            List<String> selected = new ArrayList<>();
            if (kcalBox.isSelected()) selected.add("KCAL");
            if (protBox.isSelected()) selected.add("PROT");
            if (fatBox.isSelected()) selected.add("FAT");
            if (carbBox.isSelected()) selected.add("CARB");
            if (tdfBox.isSelected()) selected.add("TDF");

            boolean isLine = lineBtn.isSelected();
            chartHolder.removeAll();
            chartHolder.add(new ChartPanel(createChart(originalMeals, swappedMeals, selected, isLine)), BorderLayout.CENTER);
            chartHolder.revalidate();
            chartHolder.repaint();
        };

        ActionListener listener = e -> updateChart.run();
        barBtn.addActionListener(listener);
        lineBtn.addActionListener(listener);
        boxes.forEach(cb -> cb.addActionListener(listener));

        updateChart.run();
        return panel;
    }

    private static JFreeChart createChart(List<Meal> originalMeals, List<Meal> swappedMeals, List<String> nutrients, boolean line) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Float> orig = new HashMap<>(), swap = new HashMap<>();
        nutrients.forEach(n -> orig.put(n, 0f));
        nutrients.forEach(n -> swap.put(n, 0f));

        for (int i = 0; i < swappedMeals.size(); i++) {
            Map<String, Float> o = MealUtils.calculateMealNutrients(originalMeals.get(i));
            Map<String, Float> s = MealUtils.calculateMealNutrients(swappedMeals.get(i));
            for (String n : nutrients) {
                orig.put(n, orig.get(n) + o.getOrDefault(n, 0f));
                swap.put(n, swap.get(n) + s.getOrDefault(n, 0f));
            }
        }

        for (String n : nutrients) {
            dataset.addValue(orig.get(n), "Original", n);
            dataset.addValue(swap.get(n), "Swapped", n);
        }

        return line
                ? ChartFactory.createLineChart("Cumulative Nutrient Comparison", "Nutrient", "Total", dataset)
                : ChartFactory.createBarChart("Cumulative Nutrient Comparison", "Nutrient", "Total", dataset);
    }
}
