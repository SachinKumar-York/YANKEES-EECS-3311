package statsVisualiser.gui;

import Models.SwapSuggestion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwapSuggestionsFrame extends JFrame {
    private final JList<String> suggestionList = new JList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public SwapSuggestionsFrame(List<SwapSuggestion> suggestions) {
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
    }
}
