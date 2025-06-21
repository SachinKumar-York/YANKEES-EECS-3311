package statsVisualiser.gui;

import DAO.FoodDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewMealsFrame extends JFrame {

    public ViewMealsFrame(int userId) {
        setTitle("Logged Meals");
        setSize(550, 450);
        setLayout(new BorderLayout(10, 10)); // adds spacing
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel(" Your Logged Meals", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Meal list area
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> mealList = new JList<>(listModel);
        mealList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mealList.setBackground(new Color(245, 250, 255));
        mealList.setCellRenderer(new HtmlListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(mealList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Load meals with calories
        FoodDAO dao = new FoodDAO();
        List<String> meals = dao.getLoggedMealsWithCaloriesForUser(userId);
        if (meals.isEmpty()) {
            listModel.addElement("<html><i>No meals logged yet.</i></html>");
        } else {
            for (String meal : meals) {
                listModel.addElement(meal);
            }
        }
    }

    // Custom renderer to properly handle HTML content and spacing
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
}
