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
import java.awt.event.ActionListener;

public class SwapVisualizationView extends JFrame {
    private final List<Meal> originalMeals;
    private final List<Meal> swappedMeals;
    private final List<Integer> mealIds;
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
        tabs.addTab("Cumulative", createCumulativePanel());
        tabs.addTab("Average", new ChartPanel(createAverageChart()));
        tabs.addTab("Compare with CFG", createCFGComparisonPanel());

        add(tabs, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private Map<Integer, String> buildMealNameMap(int userId) {
        Map<Integer, String> mealNameMap = new HashMap<>();
        List<String> loggedMeals = foodDAO.getLoggedMealsWithCaloriesForUser(userId);
        Pattern pattern = Pattern.compile("<span style='display:none'>([0-9]+)</span>.*?<b>(.*?)</b>");

        for (String mealStr : loggedMeals) {
            System.out.println("Processing meal string: " + mealStr);
            Matcher matcher = pattern.matcher(mealStr);
            if (matcher.find()) {
                System.out.println("Matched meal: " + mealStr);
                int mealId = Integer.parseInt(matcher.group(1));
                String mealName = matcher.group(2);
                mealNameMap.put(mealId, mealName);
            } else {
                System.out.println("Failed to match: " + mealStr);
            }
        }
        System.out.println("Final mealIdToNameMap: " + mealNameMap);
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

    private JPanel createCumulativePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton barBtn = new JRadioButton("Bar Chart", true);
        JRadioButton lineBtn = new JRadioButton("Line Chart");
        ButtonGroup chartTypeGroup = new ButtonGroup();
        chartTypeGroup.add(barBtn);
        chartTypeGroup.add(lineBtn);

        JCheckBox kcalBox = new JCheckBox("Calories", true);
        JCheckBox protBox = new JCheckBox("Protein", true);
        JCheckBox fatBox = new JCheckBox("Fat", true);
        JCheckBox carbBox = new JCheckBox("Carbs", true);
        JCheckBox tdfBox = new JCheckBox("Fibre", true);

        List<JCheckBox> nutrientChecks = List.of(kcalBox, protBox, fatBox, carbBox, tdfBox);

        controlPanel.add(barBtn);
        controlPanel.add(lineBtn);
        nutrientChecks.forEach(controlPanel::add);

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
            JFreeChart chart = createDynamicCumulativeChart(selected, isLine);
            chartHolder.removeAll();
            chartHolder.add(new ChartPanel(chart), BorderLayout.CENTER);
            chartHolder.revalidate();
            chartHolder.repaint();
        };

        ActionListener listener = e -> updateChart.run();
        barBtn.addActionListener(listener);
        lineBtn.addActionListener(listener);
        nutrientChecks.forEach(cb -> cb.addActionListener(listener));

        updateChart.run();
        return panel;
    }

    private JFreeChart createDynamicCumulativeChart(List<String> nutrients, boolean useLineChart) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Float> origTotal = new HashMap<>();
        Map<String, Float> swapTotal = new HashMap<>();

        for (String n : nutrients) {
            origTotal.put(n, 0f);
            swapTotal.put(n, 0f);
        }

        for (int i = 0; i < swappedMeals.size(); i++) {
            Map<String, Float> orig = MealUtils.calculateMealNutrients(originalMeals.get(i));
            Map<String, Float> swap = MealUtils.calculateMealNutrients(swappedMeals.get(i));
            for (String n : nutrients) {
                origTotal.put(n, origTotal.get(n) + orig.getOrDefault(n, 0f));
                swapTotal.put(n, swapTotal.get(n) + swap.getOrDefault(n, 0f));
            }
        }

        for (String n : nutrients) {
            dataset.addValue(origTotal.get(n), "Original", n);
            dataset.addValue(swapTotal.get(n), "Swapped", n);
        }

        return useLineChart
                ? ChartFactory.createLineChart("Cumulative Nutrient Comparison", "Nutrient", "Total", dataset)
                : ChartFactory.createBarChart("Cumulative Nutrient Comparison", "Nutrient", "Total", dataset);
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

    private JPanel createCFGComparisonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        // Calculate cumulative CFG breakdown for original and swapped meals
        Map<String, Double> origCFGBreakdown = new HashMap<>();
        Map<String, Double> swapCFGBreakdown = new HashMap<>();
        origCFGBreakdown.put("Vegetables & Fruits", 0.0);
        origCFGBreakdown.put("Whole Grains", 0.0);
        origCFGBreakdown.put("Protein Foods", 0.0);
        swapCFGBreakdown.put("Vegetables & Fruits", 0.0);
        swapCFGBreakdown.put("Whole Grains", 0.0);
        swapCFGBreakdown.put("Protein Foods", 0.0);

        // Aggregate CFG percentages across all meals
        for (int i = 0; i < originalMeals.size(); i++) {
            Map<String, Double> origMealCFG = foodDAO.getCFGBreakdownForMeal(originalMeals.get(i));
            Map<String, Double> swapMealCFG = foodDAO.getCFGBreakdownForMeal(swappedMeals.get(i));
            
            for (String group : origCFGBreakdown.keySet()) {
                origCFGBreakdown.merge(group, origMealCFG.getOrDefault(group, 0.0), Double::sum);
                swapCFGBreakdown.merge(group, swapMealCFG.getOrDefault(group, 0.0), Double::sum);
            }
        }

        // Normalize to percentages (divide by number of meals to get average contribution)
        int totalMeals = originalMeals.size();
        if (totalMeals > 0) {
            for (String group : origCFGBreakdown.keySet()) {
                origCFGBreakdown.put(group, origCFGBreakdown.get(group) / totalMeals);
                swapCFGBreakdown.put(group, swapCFGBreakdown.get(group) / totalMeals);
            }
        }

        // Create pie chart datasets
        DefaultPieDataset origDataset = new DefaultPieDataset();
        DefaultPieDataset swapDataset = new DefaultPieDataset();

        for (String group : origCFGBreakdown.keySet()) {
            double origValue = origCFGBreakdown.get(group);
            double swapValue = swapCFGBreakdown.get(group);
            if (origValue > 0) {
                origDataset.setValue(group, origValue);
            }
            if (swapValue > 0) {
                swapDataset.setValue(group, swapValue);
            }
        }

        // Create pie charts
        JFreeChart origChart = ChartFactory.createPieChart(
                "Original Meals CFG Breakdown",
                origDataset,
                true,
                true,
                false
        );

        JFreeChart swapChart = ChartFactory.createPieChart(
                "Swapped Meals CFG Breakdown",
                swapDataset,
                true,
                true,
                false
        );

        // Add charts to panel
        panel.add(new ChartPanel(origChart));
        panel.add(new ChartPanel(swapChart));

        return panel;
    }
}