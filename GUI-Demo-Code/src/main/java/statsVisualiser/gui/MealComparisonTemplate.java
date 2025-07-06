package statsVisualiser.gui;

import javax.swing.*;
import java.awt.*;

// Template Method pattern base class
public abstract class MealComparisonTemplate extends JFrame {
    protected JPanel leftPanel;
    protected JPanel rightPanel;
    protected JPanel nutrientPanel;
    protected JPanel ingredientPanel;

    protected JButton swapMealBtn;
    protected JButton visualizeBtn;
    protected JButton suggestAnotherBtn;

    public MealComparisonTemplate(String title) {
        setTitle(title);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top: two side-by-side meal panels
        JPanel mealPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        leftPanel = new JPanel(new BorderLayout());
        rightPanel = new JPanel(new BorderLayout());

        mealPanel.add(leftPanel);
        mealPanel.add(rightPanel);

        add(mealPanel, BorderLayout.NORTH);

        // Center: nutrients + ingredients breakdown
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        nutrientPanel = new JPanel(new BorderLayout());
        ingredientPanel = new JPanel(new BorderLayout());

        bottomPanel.add(nutrientPanel);
        bottomPanel.add(ingredientPanel);

        add(bottomPanel, BorderLayout.CENTER);

        // Bottom: buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        swapMealBtn = new JButton("Swap Meal");
        visualizeBtn = new JButton("Visualize");
        suggestAnotherBtn = new JButton("Suggest Another");

        buttonPanel.add(swapMealBtn);
        buttonPanel.add(visualizeBtn);
        buttonPanel.add(suggestAnotherBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Hook for subclasses
        setupButtons();

        // Call abstract methods for subclasses to implement their content
        setupMealPanels();
        setupNutrientPanel();
        setupIngredientPanel();
    }

    // Template methods (steps)
    protected abstract void setupMealPanels();
    protected abstract void setupNutrientPanel();
    protected abstract void setupIngredientPanel();
    protected abstract void setupButtons();
}
