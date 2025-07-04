package DAO;

import Models.Food;
import Models.MealIngredient;

import java.sql.*;
import java.util.*;

public class NutrientDatabase {

    // Returns a map of nutrient symbols (e.g., "KCAL", "PROT") to values scaled by quantity
    public Map<String, Float> getNutrients(Food food, double quantityInGrams) {
        Map<String, Float> nutrients = new HashMap<>();

        String sql = "SELECT nn.NutrientSymbol, na.NutrientValue " +
                     "FROM nutrientamount na " +
                     "JOIN nutrientname nn ON na.NutrientNameID = nn.NutrientNameID " +
                     "WHERE na.FoodID = ?";

        try (Connection conn = DBConnector.getConnection(DBConnector.DBType.MYSQL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, food.getFoodId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String symbol = rs.getString("NutrientSymbol");
                    float valuePer100g = rs.getFloat("NutrientValue");
                    float adjustedValue = (valuePer100g * (float) quantityInGrams) / 100f;
                    nutrients.put(symbol, adjustedValue);
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
                     "    SELECT FoodGroupID FROM foodname WHERE FoodID = ? " +
                     ") " +
                     "AND fn.FoodID != ? " +
                     "LIMIT 10";

        try (Connection conn = DBConnector.getConnection(DBConnector.DBType.MYSQL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            long originalFoodId = original.getFood().getFoodId();
            ps.setLong(1, originalFoodId);
            ps.setLong(2, originalFoodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long foodId = rs.getLong("FoodID");
                    String description = rs.getString("FoodDescription");
                    Food food = new Food(foodId, description);
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
