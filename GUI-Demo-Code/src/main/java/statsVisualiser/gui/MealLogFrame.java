package statsVisualiser.gui;

import DAO.FoodDAO;
import Models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealLogFrame extends JFrame {
    private final JComboBox<String> mealTypeBox;
    private final JSpinner datePicker;
    private final JTextField mealNameField;
    private final JComboBox<Food> foodCombo;
    private final JTextField qtyField;
    private final JButton addBtn;
    private final JButton logMealBtn;
    private final DefaultListModel<String> ingredientListModel;
    private final JList<String> ingredientJList;

    private final List<MealIngredient> selectedIngredients = new ArrayList<>();
    private final FoodDAO foodDAO = new FoodDAO();
    private final List<Food> allFoods;
    private final MealValidationContext validationContext;

    private final int userId;

    public MealLogFrame(int userId) {
        this.userId = userId;

        setTitle("Log a New Meal");
        setSize(650, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Meal Validation Strategies
        validationContext = new MealValidationContext();
        validationContext.addValidator(new SingleMealPerDayValidator(foodDAO));

        // Title label
        JLabel header = new JLabel(" Log Your Meal", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Meal Info Panel (Name, Type, Date)
        JPanel mealDetails = new JPanel(new GridLayout(3, 2, 10, 10));
        mealDetails.setBorder(BorderFactory.createTitledBorder("Meal Details"));
        mealNameField = new JTextField();
        mealTypeBox = new JComboBox<>(new String[]{"breakfast", "lunch", "snack", "dinner"});
        datePicker = new JSpinner(new SpinnerDateModel());
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "yyyy-MM-dd"));

        mealDetails.add(new JLabel("Meal Name:"));
        mealDetails.add(mealNameField);
        mealDetails.add(new JLabel("Meal Type:"));
        mealDetails.add(mealTypeBox);
        mealDetails.add(new JLabel("Meal Date:"));
        mealDetails.add(datePicker);
        add(mealDetails, BorderLayout.WEST);

        // Ingredient Input and List Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Meal Ingredients"));

        // Ingredient input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        allFoods = foodDAO.getAllFoods();
        foodCombo = new JComboBox<>(allFoods.toArray(new Food[0]));
        foodCombo.setEditable(true);
        qtyField = new JTextField(5);
        addBtn = new JButton("Add");

        inputPanel.add(new JLabel("Ingredient:"));
        inputPanel.add(foodCombo);
        inputPanel.add(new JLabel("Qty (g):"));
        inputPanel.add(qtyField);
        inputPanel.add(addBtn);

        // Ingredient list panel
        ingredientListModel = new DefaultListModel<>();
        ingredientJList = new JList<>(ingredientListModel);
        ingredientJList.setBackground(new Color(245, 250, 255));
        ingredientJList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(ingredientJList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Log meal button
        logMealBtn = new JButton("Log Meal");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(logMealBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        addBtn.addActionListener(this::handleAddIngredient);
        logMealBtn.addActionListener(this::handleLogMeal);
    }

    private void handleAddIngredient(ActionEvent e) {
        Food food = (Food) foodCombo.getSelectedItem();
        String qtyText = qtyField.getText();

        if (food == null || food.getFoodDescription() == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid food item.");
            return;
        }

        try {
            double qty = Double.parseDouble(qtyText);
            selectedIngredients.add(new MealIngredient(food, qty));
            ingredientListModel.addElement(food.getFoodDescription() + " - " + qty + "g");
            qtyField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
        }
    }

    private void handleLogMeal(ActionEvent e) {
        String mealName = mealNameField.getText();
        String mealType = (String) mealTypeBox.getSelectedItem();
        Date mealDate = (Date) datePicker.getValue();

        if (mealName.isEmpty() || selectedIngredients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a meal name and add at least one ingredient.");
            return;
        }

        boolean valid = validationContext.validate(userId, mealType, mealDate);
        if (!valid) {
            JOptionPane.showMessageDialog(this,
                String.join("\n", validationContext.getErrorMessages()),
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        foodDAO.logMeal(userId, mealName, mealType, mealDate, selectedIngredients);
        JOptionPane.showMessageDialog(this, "Meal logged successfully.");

        // Reset
        mealNameField.setText("");
        selectedIngredients.clear();
        ingredientListModel.clear();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int dummyUserId = 1;
            MealLogFrame frame = new MealLogFrame(dummyUserId);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

