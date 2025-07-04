package statsVisualiser.gui;

import DAO.FoodDAO;
import DAO.NutrientDatabase;
import Models.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddNutritionGoalFrame extends JFrame {

    // Display name → NutrientSymbol map
    private static final Map<String, String> DISPLAY_TO_SYMBOL = new LinkedHashMap<String, String>() {{
        put("Fibre", "TDF");
        put("Calories", "KCAL");
        put("Fat", "FAT");
        put("Protein", "PROT");
        put("Carbohydrate", "CARB");
    }};

    private final int userId;
    private final int mealId;

    private final JComboBox<String> nutrientBox1 = new JComboBox<>(DISPLAY_TO_SYMBOL.keySet().toArray(new String[0]));
    private final JTextField deltaField1 = new JTextField(5);
    private final JCheckBox increaseBox1 = new JCheckBox("Increase", true);

    private final JComboBox<String> nutrientBox2 = new JComboBox<>(DISPLAY_TO_SYMBOL.keySet().toArray(new String[0]));
    private final JTextField deltaField2 = new JTextField(5);
    private final JCheckBox increaseBox2 = new JCheckBox("Increase", true);
    private final JCheckBox enableSecondGoal = new JCheckBox("Add Second Goal");

    private final JPanel secondGoalPanel = new JPanel(new BorderLayout());

    private final SwapEngine swapEngine = new SwapEngine();
    private final FoodDAO foodDAO = new FoodDAO();
    private final NutrientDatabase nutrientDatabase = new NutrientDatabase();

    public AddNutritionGoalFrame(int userId, int mealId) {
        this.userId = userId;
        this.mealId = mealId;

        setTitle("Add Nutrition Goal");
        setSize(500, 460);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel fullContainer = new JPanel();
        fullContainer.setLayout(new BoxLayout(fullContainer, BoxLayout.Y_AXIS));
        fullContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(fullContainer, BorderLayout.CENTER);

        // 🟨 Display nutrient breakdown
        JPanel nutrientBreakdownPanel = new JPanel();
        nutrientBreakdownPanel.setLayout(new BoxLayout(nutrientBreakdownPanel, BoxLayout.Y_AXIS));

        Meal meal = fetchMealById(mealId);
        if (meal != null) {
            JLabel breakdownTitle = new JLabel("Original meal nutrient breakdown:");
            breakdownTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nutrientBreakdownPanel.add(breakdownTitle);

            Map<String, Float> totals = calculateMealNutrients(meal);
            for (Map.Entry<String, String> entry : DISPLAY_TO_SYMBOL.entrySet()) {
                String display = entry.getKey();
                String symbol = entry.getValue();
                float val = totals.getOrDefault(symbol, 0f);
                JLabel nutrientLabel = new JLabel(display + " (" + symbol + "): " + String.format("%.2f", val));
                nutrientBreakdownPanel.add(nutrientLabel);
            }
        } else {
            nutrientBreakdownPanel.add(new JLabel("Could not fetch nutrient data for the meal."));
        }

        fullContainer.add(nutrientBreakdownPanel);
        fullContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // 🟦 Title
        JLabel titleLabel = new JLabel("Set Your Nutrition Goal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        fullContainer.add(titleLabel);
        fullContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // 🟩 Goal input
        JPanel goalContainer = new JPanel();
        goalContainer.setLayout(new BoxLayout(goalContainer, BoxLayout.Y_AXIS));

        JLabel goal1Label = new JLabel("Add Nutrition Goal 1");
        goal1Label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        goalContainer.add(goal1Label);

        JPanel goal1Row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        goal1Row.add(new JLabel("Nutrient:"));
        goal1Row.add(nutrientBox1);
        goal1Row.add(new JLabel("Delta:"));
        goal1Row.add(deltaField1);
        goal1Row.add(increaseBox1);
        goalContainer.add(goal1Row);

        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        togglePanel.add(enableSecondGoal);
        goalContainer.add(togglePanel);

        secondGoalPanel.setVisible(false);
        JLabel goal2Label = new JLabel("Add Nutrition Goal 2");
        goal2Label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secondGoalPanel.add(goal2Label, BorderLayout.NORTH);

        JPanel goal2Row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        goal2Row.add(new JLabel("Nutrient:"));
        goal2Row.add(nutrientBox2);
        goal2Row.add(new JLabel("Delta:"));
        goal2Row.add(deltaField2);
        goal2Row.add(increaseBox2);
        secondGoalPanel.add(goal2Row, BorderLayout.CENTER);

        goalContainer.add(secondGoalPanel);

        enableSecondGoal.addActionListener(e -> {
            secondGoalPanel.setVisible(enableSecondGoal.isSelected());
            pack();
        });

        fullContainer.add(goalContainer);

        // 🟫 Bottom button panel
        JButton suggestButton = new JButton("Get Suggested Swaps");
        suggestButton.addActionListener(e -> handleGetSuggestedSwaps());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(suggestButton);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
    }

    private void handleGetSuggestedSwaps() {
        try {
            CompositeGoal compositeGoal = new CompositeGoal();

            String selected1 = (String) nutrientBox1.getSelectedItem();
            String nutrientSymbol1 = DISPLAY_TO_SYMBOL.get(selected1);
            float delta1 = Float.parseFloat(deltaField1.getText().trim());
            boolean increase1 = increaseBox1.isSelected();
            compositeGoal.addGoal(new NutritionalGoal(nutrientSymbol1, delta1, increase1));

            if (enableSecondGoal.isSelected()) {
                String selected2 = (String) nutrientBox2.getSelectedItem();
                String nutrientSymbol2 = DISPLAY_TO_SYMBOL.get(selected2);
                float delta2 = Float.parseFloat(deltaField2.getText().trim());
                boolean increase2 = increaseBox2.isSelected();
                compositeGoal.addGoal(new NutritionalGoal(nutrientSymbol2, delta2, increase2));
            }

            GoalRequest request = new GoalRequest(compositeGoal);

            Meal meal = fetchMealById(mealId);
            if (meal == null || meal.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Meal data could not be loaded or is empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<SwapSuggestion> suggestions = swapEngine.generateSwaps(meal, request);

            if (suggestions.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No suitable food swaps found for the given goals.",
                        "No Suggestions", JOptionPane.INFORMATION_MESSAGE);
            } else {
                SwapSuggestionsFrame suggestionsFrame = new SwapSuggestionsFrame(suggestions);
                suggestionsFrame.setVisible(true);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric delta values.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Goal Limit Exceeded", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Meal fetchMealById(int mealId) {
        try {
            List<MealIngredient> ingredients = foodDAO.getMealIngredients(mealId);
            if (ingredients == null || ingredients.isEmpty()) return null;
            return new Meal(ingredients);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Float> calculateMealNutrients(Meal meal) {
        Map<String, Float> totals = new java.util.HashMap<>();
        for (MealIngredient mi : meal.getItems()) {
            Map<String, Float> nutrients = nutrientDatabase.getNutrients(mi.getFood(), mi.getQuantity());
            for (String symbol : DISPLAY_TO_SYMBOL.values()) {
                float value = nutrients.getOrDefault(symbol, 0f);
                totals.put(symbol, totals.getOrDefault(symbol, 0f) + value);
            }
        }
        return totals;
    }
}
