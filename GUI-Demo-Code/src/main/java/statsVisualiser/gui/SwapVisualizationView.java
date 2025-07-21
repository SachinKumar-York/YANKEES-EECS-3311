package statsVisualiser.gui;

import Models.Meal;
import Models.MealUtils;
import DAO.FoodDAO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwapVisualizationView extends JFrame {
    private final List<Meal> originalMeals;
    private final List<Meal> swappedMeals;
    private final List<Integer> mealIds; // List of MealIDs corresponding to meals
    private final Map<Integer, String> mealIdToNameMap;
    private final FoodDAO foodDAO;

    public SwapVisualizationView(List<Meal> originalMeals, List<Meal> swappedMeals, List<Integer> mealIds, int userId) {
        super("Nutrient Change Visualization");
        this.originalMeals = originalMeals;
        this.swappedMeals = swappedMeals;
        this.mealIds = mealIds;
        this.foodDAO = new FoodDAO();
        this.mealIdToNameMap = buildMealNameMap(userId);

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

    private Map<Integer, String> buildMealNameMap(int userId) {
        Map<Integer, String> mealNameMap = new HashMap<>();
        List<String> loggedMeals = foodDAO.getLoggedMealsWithCaloriesForUser(userId);
        Pattern pattern = Pattern.compile("<span style='display:none'>([0-9]+)</span>.*?<b>(.*?)</b>");

        for (String mealStr : loggedMeals) {
            System.out.println("Processing meal string: " + mealStr); // Debug log
            Matcher matcher = pattern.matcher(mealStr);
            if (matcher.find()) {
                System.out.println("Matched meal: " + mealStr); // Debug log
                int mealId = Integer.parseInt(matcher.group(1));
                String mealName = matcher.group(2);
                mealNameMap.put(mealId, mealName);
            } else {
                System.out.println("Failed to match: " + mealStr); // Debug log
            }
        }
        System.out.println("Final mealIdToNameMap: " + mealNameMap); // Debug log
        return mealNameMap;
    }

    private JPanel createPerMealPanel() {
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

        for (int i = 0; i < swappedMeals.size(); i++) {
            Map<String, Float> orig = MealUtils.calculateMealNutrients(originalMeals.get(i));
            Map<String, Float> swap = MealUtils.calculateMealNutrients(swappedMeals.get(i));

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