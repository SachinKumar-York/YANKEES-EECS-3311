package statsVisualiser.gui;

import Models.Meal;
import Models.MealIngredient;
import Models.MealUtils;
import Models.SwapSuggestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SwapSuggestionsFrame extends JFrame {
    private final JList<String> suggestionList = new JList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    private final List<SwapSuggestion> suggestions;
    private final Meal originalMeal;

    public SwapSuggestionsFrame(List<SwapSuggestion> suggestions, Meal originalMeal) {
        this.suggestions = suggestions;
        this.originalMeal = originalMeal;

        setTitle("Suggested Food Swaps");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Suggested Food Swaps", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        suggestionList.setModel(listModel);
        suggestionList.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Fill list model with formatted suggestions
        for (SwapSuggestion s : suggestions) {
            String entry = s.getOriginal().getFoodDescription() +
                    " → " + s.getSuggested().getFoodDescription() +
                    " (" + s.getJustification() + ")";
            listModel.addElement(entry);
        }

        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Close button at bottom
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Double-click listener to open comparison view
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = suggestionList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        openComparisonView(index);
                    }
                }
            }
        });
    }

    private void openComparisonView(int index) {
        if (originalMeal == null || originalMeal.getItems() == null) {
            JOptionPane.showMessageDialog(this, "Original meal or its ingredients are not available.");
            return;
        }

        SwapSuggestion suggestion = suggestions.get(index);
        MealIngredient originalIngredient = null;

        // Find original ingredient in the meal that matches the suggestion's original food
        for (MealIngredient mi : originalMeal.getItems()) {
            if (mi.getFood().equals(suggestion.getOriginal())) {
                originalIngredient = mi;
                break;
            }
        }

        if (originalIngredient == null) {
            JOptionPane.showMessageDialog(this, "Original ingredient not found in meal.");
            return;
        }

        // Create swapped ingredient with the same quantity as original
        MealIngredient swappedIngredient = new MealIngredient(suggestion.getSuggested(), originalIngredient.getQuantity());

        // Build a swapped meal with only this ingredient replaced
        Meal swappedMeal = MealUtils.buildSwappedMeal(originalMeal, suggestion);

        // Open the comparison view window
        new SwapComparisonView(originalMeal, swappedMeal, originalIngredient, swappedIngredient);
    }
}