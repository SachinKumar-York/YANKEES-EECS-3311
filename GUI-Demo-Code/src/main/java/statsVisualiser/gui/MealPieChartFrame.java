package statsVisualiser.gui;

import DAO.FoodDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MealPieChartFrame extends JFrame {

    private final int userId;
    private final int mealId;
    private final FoodDAO dao = new FoodDAO();

    public MealPieChartFrame(int userId, int mealId) {
        this.userId = userId;
        this.mealId = mealId;

        // ✅ Get macronutrients from DAO
        Map<String, Double> nutrients = dao.getMacronutrientsForMeal(userId, mealId);

        // ✅ Print retrieved nutrients
        System.out.println("Meal ID: " + mealId + ", User ID: " + userId);
        if (nutrients.isEmpty()) {
            System.out.println("⚠️ No data found for this meal.");
        }

        // ✅ Show warning if no data
        if (nutrients.isEmpty() ||
                (nutrients.getOrDefault("PROT", 0.0) == 0 &&
                 nutrients.getOrDefault("FAT", 0.0) == 0 &&
                 nutrients.getOrDefault("CARB", 0.0) == 0)) {

            JOptionPane.showMessageDialog(
                    null,
                    "You don't have permission to view this meal or no data is available.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
            );
            return; // ❌ don't show chart
        }

        // ✅ Proceed with chart
        setTitle("Meal Nutrient Breakdown");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultPieDataset dataset = createDataset(nutrients);

        JFreeChart chart = ChartFactory.createPieChart(
                "Protein, Carbs, and Fat Breakdown",
                dataset,
                true, true, false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Protein", new Color(102, 178, 255));
        plot.setSectionPaint("Carbs", new Color(255, 204, 102));
        plot.setSectionPaint("Fat", new Color(255, 102, 102));

        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);
    }

    private DefaultPieDataset createDataset(Map<String, Double> nutrients) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        double protein = nutrients.getOrDefault("PROT", 0.0);
        double carbs = nutrients.getOrDefault("CARB", 0.0);
        double fat = nutrients.getOrDefault("FAT", 0.0);

        if (protein > 0) dataset.setValue("Protein", protein);
        if (carbs > 0) dataset.setValue("Carbs", carbs);
        if (fat > 0) dataset.setValue("Fat", fat);

        return dataset;
    }
}
