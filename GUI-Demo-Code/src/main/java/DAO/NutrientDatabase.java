package DAO;

import Models.Food;
import Models.MealIngredient;

import java.sql.*;
import java.util.*;

public class NutrientDatabase {

    // Returns a map of nutrient tag 
    public Map<String, Float> getNutrients(Food food, double quantityInGrams) {
        Map<String, Float> nutrients = new HashMap<>();

        String sql = "SELECT nn.Tagname, na.NutrientValue " +
                "FROM nutrientamount na " +
                "JOIN nutrientname nn ON na.NutrientNameID = nn.NutrientNameID " +
                "WHERE na.FoodID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, food.getFoodId());  // corrected method name

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tag = rs.getString("Tagname");
                    float valuePer100g = rs.getFloat("NutrientValue");
                    // Adjust nutrient value proportionally to the quantity
                    float adjustedValue = (valuePer100g * (float) quantityInGrams) / 100f;
                    nutrients.put(tag, adjustedValue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nutrients;
    }

    // Returns a list of MealIngredients from the same food group (excluding the original food)
    public List<MealIngredient> getSimilarItems(MealIngredient original) {
        List<MealIngredient> alternatives = new ArrayList<>();

        String sql = "SELECT fn.FoodID, fn.FoodDescription " +
                "FROM foodname fn " +
                "WHERE fn.FoodGroupID = ( " +
                "    SELECT FoodGroupID " +
                "    FROM foodname " +
                "    WHERE FoodID = ? " +
                ") " +
                "AND fn.FoodID != ? " +
                "LIMIT 10";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            long originalFoodId = original.getFood().getFoodId();  // corrected method name
            ps.setLong(1, originalFoodId);
            ps.setLong(2, originalFoodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long foodId = rs.getLong("FoodID");
                    String description = rs.getString("FoodDescription");
                    Food food = new Food(foodId, description);
                    // Use same quantity as original for initial suggestion
                    MealIngredient mi = new MealIngredient(food, original.getQuantity());
                    alternatives.add(mi);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alternatives;
    }
}
