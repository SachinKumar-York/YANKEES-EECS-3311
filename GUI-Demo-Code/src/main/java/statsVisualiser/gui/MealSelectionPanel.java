package statsVisualiser.gui;

import DAO.FoodDAO;
import Models.Meal;
import Models.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class MealSelectionPanel extends JPanel {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> mealList = new JList<>(listModel);

    public MealSelectionPanel(int userId, Consumer<Integer> onMealDoubleClick) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

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
                        if (mealId > 0) {
                            onMealDoubleClick.accept(mealId);
                        }
                    }
                }
            }
        });
    }

    public int getSelectedMealId() {
        int index = mealList.getSelectedIndex();
        if (index == -1) return -1;

        String selectedValue = listModel.get(index);
        return extractMealIdFromHtml(selectedValue);
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
}
