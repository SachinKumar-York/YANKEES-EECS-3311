package statsVisualiser.gui;

import DAO.FoodDAO;
import DAO.UserProfileDao;
import Models.UserProfile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class RecommendedComparisonFrame extends JFrame {

    private static final Set<String> KEY_NUTRIENTS = new HashSet<>(Arrays.asList(
        "PROTEIN",
        "CARBOHYDRATE, TOTAL (BY DIFFERENCE)",
        "FAT (TOTAL LIPIDS)",
        "FIBRE, TOTAL DIETARY",
        "ENERGY (KILOCALORIES)",
        "SUCROSE"
    ));

    public RecommendedComparisonFrame(int userId, Map<String, Double> userAvg, Date startDate, Date endDate) {
        setTitle("Comparison with Recommended Daily Portions");
        setSize(900, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        UserProfile profile = new UserProfileDao().getUserProfile(userId);
        Map<String, Double> recommended = computeDynamicRecommendations(profile);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        StringBuilder summary = new StringBuilder("<html><b>Difference Summary:</b><br><br>");

        for (String nutrient : KEY_NUTRIENTS) {
            double userVal = userAvg.getOrDefault(nutrient, 0.0);
            double recVal = recommended.getOrDefault(nutrient, 0.0);
            double diff = userVal - recVal;

            dataset.addValue(userVal, "Your Avg Intake", nutrient);
            dataset.addValue(recVal, "Recommended", nutrient);

            String status = diff > 0 ? String.format("Above by %.1f", diff)
                          : diff < 0 ? String.format("Below by %.1f", -diff)
                          : "Exactly Met";
            summary.append(nutrient).append(": ").append(status).append("<br>");
        }
        summary.append("</html>");

        JFreeChart chart = ChartFactory.createBarChart(
            "Your Daily Intake vs Recommended",
            "Nutrient",
            "Amount (g or kcal)",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JLabel summaryLabel = new JLabel(summary.toString());
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton cfgButton = new JButton("Compare with CFG (Canada Food Guide)");
        cfgButton.addActionListener(e -> {
            FoodDAO dao = new FoodDAO();
            Map<String, Double> userGroupPercentages = dao.getUserFoodGroupPercentages(userId, startDate, endDate);
            new CFGComparisonFrame(userGroupPercentages).setVisible(true);
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(summaryLabel, BorderLayout.CENTER);
        southPanel.add(cfgButton, BorderLayout.SOUTH);

        add(chartPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private Map<String, Double> computeDynamicRecommendations(UserProfile p) {
        Map<String, Double> rec = new LinkedHashMap<>();

        double weight = p.getWeight();
        double height = p.getHeight();
        int age = p.getAge();
        String gender = p.getSex().toLowerCase();
        double activity = 1.55;

        double bmr;
        if (gender.equals("male")) {
            bmr = 10 * weight + 6.25 * height * 100 - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height * 100 - 5 * age - 161;
        }
        double tdee = bmr * activity;

        rec.put("ENERGY (KILOCALORIES)", tdee);
        rec.put("PROTEIN", gender.equals("male") ? weight * 1.0 : weight * 0.8);
        rec.put("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", gender.equals("male") ? weight * 5 : weight * 4);
        rec.put("FAT (TOTAL LIPIDS)", gender.equals("male") ? weight * 1.0 : weight * 0.8);
        rec.put("FIBRE, TOTAL DIETARY", gender.equals("male") ? 38.0 : 25.0);
        rec.put("SUCROSE", gender.equals("male") ? 36.0 : 25.0);

        return rec;
    }
}
