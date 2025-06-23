package statsVisualiser.gui;

import DAO.FoodDAO;
import Models.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ViewMealsFrame extends JFrame {

    public ViewMealsFrame(int userId) {
        setTitle("Logged Meals");
        setSize(550, 450);
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

        // Handle double-click
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
                            System.out.println(mealId+""+currentUserId);
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
            int dummyUserId = 1; // Replace with a valid userId if needed
            ViewMealsFrame frame = new ViewMealsFrame(dummyUserId);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

}
