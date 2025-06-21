//package statsVisualiser.gui;
//
//import DAO.FoodDAO;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.List;
//
//public class ViewMealsFrame extends JFrame {
//
//    public ViewMealsFrame(int userId) {
//        setTitle("Logged Meals");
//        setSize(550, 450);
//        setLayout(new BorderLayout(10, 10)); // adds spacing
//        setLocationRelativeTo(null);
//        getContentPane().setBackground(Color.WHITE);
//
//        // Title
//        JLabel titleLabel = new JLabel(" Your Logged Meals", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//        add(titleLabel, BorderLayout.NORTH);
//
//        // Meal list area
//        DefaultListModel<String> listModel = new DefaultListModel<>();
//        JList<String> mealList = new JList<>(listModel);
//        mealList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        mealList.setBackground(new Color(245, 250, 255));
//        mealList.setCellRenderer(new HtmlListCellRenderer());
//
//        JScrollPane scrollPane = new JScrollPane(mealList);
//        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
//        add(scrollPane, BorderLayout.CENTER);
//
//        // Load meals with calories
//        FoodDAO dao = new FoodDAO();
//        List<String> meals = dao.getLoggedMealsWithCaloriesForUser(userId);
//        if (meals.isEmpty()) {
//            listModel.addElement("<html><i>No meals logged yet.</i></html>");
//        } else {
//            for (String meal : meals) {
//                listModel.addElement(meal);
//            }
//        }
//    }
//
//    // Custom renderer to properly handle HTML content and spacing
//    private static class HtmlListCellRenderer extends DefaultListCellRenderer {
//        @Override
//        public Component getListCellRendererComponent(
//                JList<?> list, Object value, int index,
//                boolean isSelected, boolean cellHasFocus) {
//
//            JLabel label = (JLabel) super.getListCellRendererComponent(
//                    list, value, index, isSelected, cellHasFocus);
//
//            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//            return label;
//        }
//    }
//}

package statsVisualiser.gui;

import DAO.FoodDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

        // ✅ Added: Handle double-click to open pie chart window
        mealList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = mealList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        String selectedValue = listModel.get(index);
                        int mealId = extractMealIdFromHtml(selectedValue);
                        // Pass userId and mealId to the chart frame
                        SwingUtilities.invokeLater(() -> new MealPieChartFrame(mealId).setVisible(true));
                    }
                }
            }
        });

    }

    // ✅ Added: Extract MealID from hidden span in HTML string
    private int extractMealIdFromHtml(String htmlString) {
        try {
            int start = htmlString.indexOf("<span style='display:none'>") + 28;
            int end = htmlString.indexOf("</span>", start);
            return Integer.parseInt(htmlString.substring(start, end));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
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
