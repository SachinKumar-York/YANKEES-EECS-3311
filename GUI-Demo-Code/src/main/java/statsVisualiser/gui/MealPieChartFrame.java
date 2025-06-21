package statsVisualiser.gui;

import DAO.DBConnector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MealPieChartFrame extends JFrame {

    private int mealId;

    public MealPieChartFrame(int mealId) {
        this.mealId = mealId;

        setTitle("Meal Nutrient Breakdown");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultPieDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createPieChart(
                "Protein, Carbs, and Fat Breakdown",
                dataset,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Protein", new Color(102, 178, 255));
        plot.setSectionPaint("Carbs", new Color(255, 204, 102));
        plot.setSectionPaint("Fat", new Color(255, 102, 102));

        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);
    }

    private DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        double protein = 0;
        double fat = 0;
        double carbs = 0;

        String sql =
                "SELECT " +
                "    nn.NutrientSymbol, " +
                "    ROUND(SUM((IFNULL(na.NutrientValue, 0) / 100) * mi.Qty_grams), 2) AS total_amount " +
                "FROM mealingredient mi " +
                "JOIN nutrientamount na ON mi.FoodID = na.FoodID " +
                "JOIN nutrientname nn ON na.NutrientNameID = nn.NutrientNameID " +
                "WHERE mi.MealID = ? " +
                "  AND nn.NutrientSymbol IN ('PROT', 'FAT', 'CARB') " +
                "GROUP BY nn.NutrientSymbol";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mealId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nutrient = rs.getString("NutrientSymbol");
                    double amount = rs.getDouble("total_amount");

                    switch (nutrient) {
                    case "PROT":
                        protein = amount;
                        break;
                    case "FAT":
                        fat = amount;
                        break;
                    case "CARB":
                        carbs = amount;
                        break;
                }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Protein: " + protein + ", Fat: " + fat + ", Carbs: " + carbs);

        if (protein > 0) dataset.setValue("Protein", protein);
        if (carbs > 0) dataset.setValue("Carbs", carbs);
        if (fat > 0) dataset.setValue("Fat", fat);

        // Show a message if all are zero
        if (protein == 0 && fat == 0 && carbs == 0) {
            JOptionPane.showMessageDialog(this,
                    "No macronutrient data found for this meal.",
                    "Data Not Found",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        return dataset;
    }
}
