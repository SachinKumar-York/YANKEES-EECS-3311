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
        setSize(1200, 800);  // Increased size here
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        // Meal Validation Strategies
        validationContext = new MealValidationContext();
        validationContext.addValidator(new SingleMealPerDayValidator(foodDAO));

        // Title label
        JLabel header = new JLabel("Log Your Meal", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Meal Info Panel (Name, Type, Date)
        JPanel mealDetails = new JPanel(new GridBagLayout());
        mealDetails.setBackground(Color.WHITE);
        mealDetails.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 255), 2),
            "Meal Details",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(50, 50, 150)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel mealNameLabel = new JLabel("Meal Name:");
        mealNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mealNameField = new JTextField(20);
        mealNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel mealTypeLabel = new JLabel("Meal Type:");
        mealTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mealTypeBox = new JComboBox<>(new String[]{"breakfast", "lunch", "snack", "dinner"});
        mealTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel dateLabel = new JLabel("Meal Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        datePicker = new JSpinner(new SpinnerDateModel());
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "yyyy-MM-dd"));
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        mealDetails.add(mealNameLabel, gbc);
        gbc.gridx = 1;
        mealDetails.add(mealNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mealDetails.add(mealTypeLabel, gbc);
        gbc.gridx = 1;
        mealDetails.add(mealTypeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mealDetails.add(dateLabel, gbc);
        gbc.gridx = 1;
        mealDetails.add(datePicker, gbc);

        add(mealDetails, BorderLayout.WEST);

        // Ingredient Input and List Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 255), 2),
            "Meal Ingredients",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(50, 50, 150)
        ));
        centerPanel.setBackground(Color.WHITE);

        // Ingredient input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBackground(Color.WHITE);
        allFoods = foodDAO.getAllFoods();
        foodCombo = new JComboBox<>(allFoods.toArray(new Food[0]));
        foodCombo.setEditable(true);
        foodCombo.setPreferredSize(new Dimension(250, 28));
        foodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        qtyField = new JTextField(6);
        qtyField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addBtn = new JButton("Add");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setBackground(new Color(60, 120, 215));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        inputPanel.add(new JLabel("Ingredient:"));
        inputPanel.add(foodCombo);
        inputPanel.add(new JLabel("Qty (g):"));
        inputPanel.add(qtyField);
        inputPanel.add(addBtn);

        // Ingredient list panel
        ingredientListModel = new DefaultListModel<>();
        ingredientJList = new JList<>(ingredientListModel);
        ingredientJList.setBackground(new Color(240, 245, 255));
        ingredientJList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ingredientJList.setFixedCellHeight(25);
        JScrollPane scrollPane = new JScrollPane(ingredientJList);
        scrollPane.setPreferredSize(new Dimension(350, 250));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 255)));

        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Log meal button
        logMealBtn = new JButton("Log Meal");
        logMealBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logMealBtn.setBackground(new Color(0, 100, 0));
        logMealBtn.setForeground(Color.WHITE);
        logMealBtn.setFocusPainted(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
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
