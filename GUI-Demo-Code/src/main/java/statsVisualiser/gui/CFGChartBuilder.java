//CFGChartBuilder.java start

package statsVisualiser.gui;

import DAO.FoodDAO;
import Models.Meal;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFGChartBuilder {
    public static JPanel createCFGComparisonPanel(List<Meal> originalMeals, List<Meal> swappedMeals, FoodDAO foodDAO) {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        Map<String, Double> orig = computeCFGTotals(originalMeals, foodDAO);
        Map<String, Double> swap = computeCFGTotals(swappedMeals, foodDAO);
        panel.add(buildPieChart("Original Meals CFG Breakdown (%)", orig));
        panel.add(buildPieChart("Swapped Meals CFG Breakdown (%)", swap));
        return panel;
    }

    private static Map<String, Double> computeCFGTotals(List<Meal> meals, FoodDAO dao) {
        Map<String, Double> totals = new HashMap<>();
        totals.put("Vegetables & Fruits", 0.0);
        totals.put("Whole Grains", 0.0);
        totals.put("Protein Foods", 0.0);
        for (Meal meal : meals) {
            Map<String, Double> raw = dao.getCFGRawTotalsForMeal(meal);
            for (String k : raw.keySet()) {
                totals.merge(k, raw.getOrDefault(k, 0.0), Double::sum);
            }
        }
        return totals;
    }

    private static ChartPanel buildPieChart(String title, Map<String, Double> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        data.forEach((k, v) -> {
            if (v > 0) dataset.setValue(k, (v / total) * 100);
        });
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        return new ChartPanel(chart);
    }
}
