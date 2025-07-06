package statsVisualiser.gui;

import Models.Meal;
import Models.MealUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class SwapVisualizationView extends JFrame {
    private final Meal originalMeal;
    private final List<Meal> swappedMeals;

    public SwapVisualizationView(Meal originalMeal, List<Meal> swappedMeals) {
        super("Nutrient Change Visualization");
        this.originalMeal = originalMeal;
        this.swappedMeals = swappedMeals;

        setSize(1000, 750);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Per Meal", createPerMealPanel());
        tabs.addTab("Cumulative", new ChartPanel(createCumulativeChart()));
        tabs.addTab("Average", new ChartPanel(createAverageChart()));

        add(tabs, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel createPerMealPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < swappedMeals.size(); i++) {
            listModel.addElement("Meal " + (i + 1));
        }

        JList<String> mealList = new JList<>(listModel);
        mealList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(mealList);
        panel.add(new JLabel("Double click a meal to compare:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        mealList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = mealList.locationToIndex(evt.getPoint());
                    Meal swapped = swappedMeals.get(index);
                    showMealComparisonChart(originalMeal, swapped);
                }
            }
        });

        return panel;
    }

    private void showMealComparisonChart(Meal original, Meal swapped) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Float> origNutrients = MealUtils.calculateMealNutrients(original);
        Map<String, Float> swapNutrients = MealUtils.calculateMealNutrients(swapped);

        for (String key : origNutrients.keySet()) {
            dataset.addValue(origNutrients.getOrDefault(key, 0f), "Original", key);
            dataset.addValue(swapNutrients.getOrDefault(key, 0f), "Swapped", key);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Original vs Swapped Meal Nutrients",
                "Nutrient",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        JFrame frame = new JFrame("Meal Comparison");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new ChartPanel(chart));
        frame.setVisible(true);
    }

    private JFreeChart createCumulativeChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float kcalOrig = 0, protOrig = 0, fatOrig = 0, carbOrig = 0, tdfOrig = 0;
        float kcalSwap = 0, protSwap = 0, fatSwap = 0, carbSwap = 0, tdfSwap = 0;

        for (Meal swapped : swappedMeals) {
            Map<String, Float> orig = MealUtils.calculateMealNutrients(originalMeal);
            Map<String, Float> swap = MealUtils.calculateMealNutrients(swapped);

            kcalOrig += orig.getOrDefault("KCAL", 0f);
            protOrig += orig.getOrDefault("PROT", 0f);
            fatOrig += orig.getOrDefault("FAT", 0f);
            carbOrig += orig.getOrDefault("CARB", 0f);
            tdfOrig += orig.getOrDefault("TDF", 0f);

            kcalSwap += swap.getOrDefault("KCAL", 0f);
            protSwap += swap.getOrDefault("PROT", 0f);
            fatSwap += swap.getOrDefault("FAT", 0f);
            carbSwap += swap.getOrDefault("CARB", 0f);
            tdfSwap += swap.getOrDefault("TDF", 0f);
        }

        dataset.addValue(kcalOrig, "Original", "KCAL");
        dataset.addValue(protOrig, "Original", "PROT");
        dataset.addValue(fatOrig, "Original", "FAT");
        dataset.addValue(carbOrig, "Original", "CARB");
        dataset.addValue(tdfOrig, "Original", "TDF");

        dataset.addValue(kcalSwap, "Swapped", "KCAL");
        dataset.addValue(protSwap, "Swapped", "PROT");
        dataset.addValue(fatSwap, "Swapped", "FAT");
        dataset.addValue(carbSwap, "Swapped", "CARB");
        dataset.addValue(tdfSwap, "Swapped", "TDF");

        return ChartFactory.createBarChart("Cumulative Nutrients Comparison", "Nutrient", "Total",
                dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    private JFreeChart createAverageChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        float kcal = 0, prot = 0, fat = 0, carb = 0, tdf = 0;

        for (Meal meal : swappedMeals) {
            Map<String, Float> n = MealUtils.calculateMealNutrients(meal);
            kcal += n.getOrDefault("KCAL", 0f);
            prot += n.getOrDefault("PROT", 0f);
            fat += n.getOrDefault("FAT", 0f);
            carb += n.getOrDefault("CARB", 0f);
            tdf += n.getOrDefault("TDF", 0f);
        }

        int total = swappedMeals.size();
        if (total > 0) {
            dataset.setValue("KCAL", kcal / total);
            dataset.setValue("PROT", prot / total);
            dataset.setValue("FAT", fat / total);
            dataset.setValue("CARB", carb / total);
            dataset.setValue("TDF", tdf / total);
        }

        return ChartFactory.createPieChart("Average Nutrient Distribution (Swapped Meals)", dataset, true, true, false);
    }
}
