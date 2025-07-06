package statsVisualiser.gui;

import Models.Meal;
import Models.MealIngredient;
import Models.MealUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;
import java.util.List;

public class SwapComparisonView extends JFrame {
    private final Meal originalMeal;
    private final Meal swappedMeal;
    private final MealIngredient originalIngredient;
    private final MealIngredient swappedIngredient;

    public SwapComparisonView(Meal originalMeal, Meal swappedMeal,
                             MealIngredient originalIngredient, MealIngredient swappedIngredient) {
        super("Meal Swap Comparison");
        this.originalMeal = originalMeal;
        this.swappedMeal = swappedMeal;
        this.originalIngredient = originalIngredient;
        this.swappedIngredient = swappedIngredient;

        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setupMealPanels();
        setupIngredientComparisonPanel();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void setupMealPanels() {
        JPanel mealPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel originalMealPanel = new JPanel(new BorderLayout(5, 5));
        originalMealPanel.setBorder(BorderFactory.createTitledBorder("Original Meal"));

        JTextArea originalNutrientsArea = new JTextArea(formatMealNutrients(originalMeal));
        originalNutrientsArea.setEditable(false);
        originalNutrientsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        originalMealPanel.add(new JScrollPane(originalNutrientsArea), BorderLayout.NORTH);

        JList<String> originalIngredientsList = new JList<>(MealUtils.formatIngredientList(originalMeal));
        originalIngredientsList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        originalIngredientsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (originalIngredient != null && value != null && 
                    value.toString().contains(originalIngredient.getFood().getFoodDescription())) {
                    c.setForeground(Color.BLUE);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        originalMealPanel.add(new JScrollPane(originalIngredientsList), BorderLayout.CENTER);

        JPanel swappedMealPanel = new JPanel(new BorderLayout(5, 5));
        swappedMealPanel.setBorder(BorderFactory.createTitledBorder("Swapped Meal"));

        JTextArea swappedNutrientsArea = new JTextArea(formatMealNutrients(swappedMeal));
        swappedNutrientsArea.setEditable(false);
        swappedNutrientsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        swappedMealPanel.add(new JScrollPane(swappedNutrientsArea), BorderLayout.NORTH);

        JList<String> swappedIngredientsList = new JList<>(MealUtils.formatIngredientList(swappedMeal));
        swappedIngredientsList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        swappedIngredientsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (swappedIngredient != null && value != null && 
                    value.toString().contains(swappedIngredient.getFood().getFoodDescription())) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        swappedMealPanel.add(new JScrollPane(swappedIngredientsList), BorderLayout.CENTER);

        mealPanel.add(originalMealPanel);
        mealPanel.add(swappedMealPanel);
        add(mealPanel, BorderLayout.NORTH);
    }

    private void setupIngredientComparisonPanel() {
        JPanel nutrientImpactPanel = new JPanel(new BorderLayout(10, 10));
        nutrientImpactPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Impact Comparison"));

        JPanel nutrientComparison = new JPanel(new GridLayout(1, 2, 10, 0));

        JTextArea originalNutrientImpactArea = new JTextArea(formatNutrientImpact(originalIngredient));
        originalNutrientImpactArea.setEditable(false);
        originalNutrientImpactArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        originalNutrientImpactArea.setBorder(BorderFactory.createTitledBorder("Original Ingredient Nutrients"));
        nutrientComparison.add(new JScrollPane(originalNutrientImpactArea));

        JTextArea swappedNutrientImpactArea = new JTextArea(formatNutrientImpact(swappedIngredient));
        swappedNutrientImpactArea.setEditable(false);
        swappedNutrientImpactArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        swappedNutrientImpactArea.setBorder(BorderFactory.createTitledBorder("Swapped Ingredient Nutrients"));
        nutrientComparison.add(new JScrollPane(swappedNutrientImpactArea));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton applySwapButton = new JButton("Apply Swap");
        JButton tryAnotherSwapButton = new JButton("Try Another Swap");

        applySwapButton.addActionListener(e -> {
            new SwapOptionsView(originalMeal, swappedMeal, originalIngredient, swappedIngredient).setVisible(true);
        });

       

        tryAnotherSwapButton.addActionListener(e -> dispose());

        buttonsPanel.add(applySwapButton);
        buttonsPanel.add(tryAnotherSwapButton);

        nutrientImpactPanel.add(nutrientComparison, BorderLayout.CENTER);
        nutrientImpactPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(nutrientImpactPanel, BorderLayout.CENTER);
    }

    private String formatMealNutrients(Meal meal) {
        Map<String, Float> nutrients = MealUtils.calculateMealNutrients(meal);
        StringBuilder sb = new StringBuilder();
        sb.append("Nutrient Breakdown:\n");
        nutrients.forEach((symbol, value) -> sb.append(symbol).append(": ").append(String.format("%.2f", value)).append("\n"));
        return sb.toString();
    }

    private String formatNutrientImpact(MealIngredient mi) {
        if (mi == null) return "No data";
        Map<String, Float> nutrients = MealUtils.getNutrients(mi.getFood(), mi.getQuantity());
        StringBuilder sb = new StringBuilder();
        List<String> IMPACT_NUTRIENTS = List.of("KCAL", "PROT", "FAT", "TDF", "CARB");
        for (String nutrient : IMPACT_NUTRIENTS) {
            float value = nutrients.getOrDefault(nutrient, 0f);
            sb.append(nutrient).append(": ").append(String.format("%.2f", value)).append("\n");
        }
        return sb.toString();
    }
}