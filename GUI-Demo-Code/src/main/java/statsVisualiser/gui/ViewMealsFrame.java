package statsVisualiser.gui;

import DAO.FoodDAO;
import Models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

public class ViewMealsFrame extends JFrame {

    public ViewMealsFrame(int userId) {
        setTitle("Logged Meals");
        setSize(600, 550);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(" Your Logged Meals", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> mealList = new JList<>(listModel);
        mealList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mealList.setBackground(new Color(245, 250, 255));
        mealList.setCellRenderer(new HtmlListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(mealList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        FoodDAO dao = new FoodDAO();
        List<String> meals = dao.getLoggedMealsWithCaloriesForUser(userId);
        if (meals.isEmpty()) {
            listModel.addElement("<html><i>No meals logged yet.</i></html>");
        } else {
            for (String meal : meals) {
                listModel.addElement(meal);
            }
        }

        mealList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = mealList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        String selectedValue = listModel.get(index);
                        int mealId = extractMealIdFromHtml(selectedValue);

                        if (mealId > 0 && Session.isLoggedIn()) {
                            int currentUserId = Session.getCurrentUserId();
                            new MealPieChartFrame(currentUserId, mealId).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(ViewMealsFrame.this,
                                    "User session is invalid or Meal ID is missing.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Suggest Swaps Panel 
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        String[] nutrients = {"FIBR", "ENERC_KCAL", "FAT", "PROT", "CARB"};
        JComboBox<String> nutrientSelector = new JComboBox<>(nutrients);
        JTextField deltaField = new JTextField(5);
        JCheckBox increaseBox = new JCheckBox("Increase", true);
        JButton suggestSwapBtn = new JButton("Suggest Food Swaps");

        bottomPanel.add(new JLabel("Goal Nutrient:"));
        bottomPanel.add(nutrientSelector);
        bottomPanel.add(new JLabel("Delta:"));
        bottomPanel.add(deltaField);
        bottomPanel.add(increaseBox);
        bottomPanel.add(suggestSwapBtn);

        suggestSwapBtn.addActionListener(e -> {
            int index = mealList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Please select a meal to get suggestions.");
                return;
            }

            String selectedValue = listModel.get(index);
            int mealId = extractMealIdFromHtml(selectedValue);

            if (mealId > 0 && Session.isLoggedIn()) {
                int uid = Session.getCurrentUserId();

         
                Meal meal = new Meal(List.of(new MealIngredient(new Food(123L, "White Bread"), 1.0)));

                try {
                    String nutrient = (String) nutrientSelector.getSelectedItem();
                    float delta = Float.parseFloat(deltaField.getText());
                    boolean increase = increaseBox.isSelected();

                    List<NutritionalGoal> goals = List.of(new NutritionalGoal(nutrient, delta, increase));
                    GoalRequest request = new GoalRequest(goals);
                    List<SwapSuggestion> suggestions = new SwapEngine().generateSwaps(meal, request);

                    if (suggestions.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No suitable food swaps found.");
                    } else {
                        StringBuilder sb = new StringBuilder("<html><ul>");
                        for (SwapSuggestion s : suggestions) {
                            sb.append("<li><b>").append(s.getOriginal().getFoodDescription())
                              .append("</b> → <b>").append(s.getSuggested().getFoodDescription())
                              .append("</b>: ").append(s.getJustification()).append("</li>");
                        }
                        sb.append("</ul></html>");
                        JOptionPane.showMessageDialog(this, sb.toString(), "Food Swap Suggestions", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid delta value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private int extractMealIdFromHtml(String htmlString) {
        try {
            String marker = "<span style='display:none'>";
            int start = htmlString.indexOf(marker);
            if (start == -1) return -1;

            start += marker.length();
            int end = htmlString.indexOf("</span>", start);
            if (end == -1) return -1;

            String idStr = htmlString.substring(start, end).trim();
            return Integer.parseInt(idStr);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static class HtmlListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int dummyUserId = 1;
            ViewMealsFrame frame = new ViewMealsFrame(dummyUserId);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
