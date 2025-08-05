//MealChartGenerator.java start

package statsVisualiser.gui;

import Models.Meal;
import Models.MealUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class MealChartGenerator {
    public static JPanel createPerMealPanel(List<Meal> originalMeals, List<Meal> swappedMeals, List<Integer> mealIds, Map<Integer, String> mealIdToNameMap) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < swappedMeals.size(); i++) {
            int mealId = mealIds.get(i);
            String mealName = mealIdToNameMap.getOrDefault(mealId, "Unknown Meal");
            listModel.addElement(mealName);
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
                    Meal original = originalMeals.get(index);
                    Meal swapped = swappedMeals.get(index);
                    showMealComparisonChart(original, swapped);
                }
            }
        });

        return panel;
    }

    public static void showMealComparisonChart(Meal original, Meal swapped) {
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
                dataset
        );

        JFrame frame = new JFrame("Meal Comparison");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new ChartPanel(chart));
        frame.setVisible(true);
    }

    public static JFreeChart createAverageChart(List<Meal> swappedMeals) {
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
