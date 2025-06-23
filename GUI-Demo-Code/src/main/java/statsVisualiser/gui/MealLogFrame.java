package statsVisualiser.gui;

import DAO.FoodDAO;
import Models.Food;
import Models.MealIngredient;
import Models.Session;

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

    private final int userId;

    public MealLogFrame(int userId) {
        this.userId = userId;

        setTitle("Meal Logger");
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);  // So it doesn’t kill the app
        setLayout(new BorderLayout());

        // Meal details panel
        JPanel mealDetails = new JPanel(new GridLayout(3, 2));
        mealDetails.add(new JLabel("Meal Name:"));
        mealNameField = new JTextField();
        mealDetails.add(mealNameField);

        mealDetails.add(new JLabel("Meal Type:"));
        mealTypeBox = new JComboBox<>(new String[]{"breakfast", "lunch", "snack", "dinner"});
        mealDetails.add(mealTypeBox);

        mealDetails.add(new JLabel("Meal Date:"));
        datePicker = new JSpinner(new SpinnerDateModel());
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "yyyy-MM-dd"));
        mealDetails.add(datePicker);

        add(mealDetails, BorderLayout.NORTH);

        // Food selector
        JPanel foodPanel = new JPanel();
        foodPanel.add(new JLabel("Ingredient:"));
        allFoods = foodDAO.getAllFoods();
        foodCombo = new JComboBox<>(allFoods.toArray(new Food[0]));
        foodCombo.setEditable(true);
        foodPanel.add(foodCombo);

        foodPanel.add(new JLabel("Qty (g):"));
        qtyField = new JTextField(5);
        foodPanel.add(qtyField);

        addBtn = new JButton("Add Ingredient");
        foodPanel.add(addBtn);

        add(foodPanel, BorderLayout.CENTER);

        // Ingredient list
        ingredientListModel = new DefaultListModel<>();
        ingredientJList = new JList<>(ingredientListModel);
        add(new JScrollPane(ingredientJList), BorderLayout.EAST);

        // Submit
        logMealBtn = new JButton("Log Meal");
        add(logMealBtn, BorderLayout.SOUTH);

        // Event listeners
        addBtn.addActionListener(this::handleAddIngredient);
        logMealBtn.addActionListener(this::handleLogMeal);
    }

    private void handleAddIngredient(ActionEvent e) {
        Food food = (Food) foodCombo.getSelectedItem();
        String qtyText = qtyField.getText();

        try {
            double qty = Double.parseDouble(qtyText);
            selectedIngredients.add(new MealIngredient(food, qty));
            ingredientListModel.addElement(food.getFoodDescription() + " - " + qty + "g");
            qtyField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid quantity.");
        }
    }

    private void handleLogMeal(ActionEvent e) {
        String mealName = mealNameField.getText();
        String mealType = (String) mealTypeBox.getSelectedItem();
        Date mealDate = (Date) datePicker.getValue();

        if (mealName.isEmpty() || selectedIngredients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a meal name and at least one ingredient.");
            return;
        }

        // Restrict logging to one meal per type (except snack)
        if (!mealType.equalsIgnoreCase("snack")) {
            boolean alreadyLogged = foodDAO.hasMealTypeLogged(userId, mealType, mealDate);
            if (alreadyLogged) {
                JOptionPane.showMessageDialog(this,
                        "You have already logged a " + mealType + " for this date.",
                        "Meal Already Logged", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        foodDAO.logMeal(userId, mealName, mealType, mealDate, selectedIngredients);
        JOptionPane.showMessageDialog(this, "Meal logged successfully.");
        mealNameField.setText("");
        selectedIngredients.clear();
        ingredientListModel.clear();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int dummyUserId = 1; // Replace with a valid user ID from your DB if needed
            new MealLogFrame(dummyUserId).setVisible(true);
        });
    }

}
