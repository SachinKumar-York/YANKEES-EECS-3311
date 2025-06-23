package DAO;

import Models.Food;
import Models.MealIngredient;
import DAO.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodDAO {

    public List<Food> getAllFoods() {
        List<Food> foods = new ArrayList<>();

        String sql = "SELECT FoodID, FoodDescription FROM FoodName ORDER BY FoodDescription";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                foods.add(new Food(rs.getLong("FoodID"), rs.getString("FoodDescription")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foods;
    }

    public void logMeal(int userId, String mealName, String mealType, Date mealDate, List<MealIngredient> ingredients) {
        String insertMeal = "INSERT INTO Meal (meal_name, MealDate, MealType) VALUES (?, ?, ?)";
        String insertUserMeal = "INSERT INTO UserMeal (user_id, MealID) VALUES (?, ?)";
        String insertMealIngredient = "INSERT INTO MealIngredient (MealID, FoodID, Qty_grams) VALUES (?, ?, ?)";

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);

            // Insert Meal
            int mealId;
            try (PreparedStatement stmt = conn.prepareStatement(insertMeal, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, mealName);
                stmt.setDate(2, new java.sql.Date(mealDate.getTime()));
                stmt.setString(3, mealType);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        mealId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        return;
                    }
                }
            }

            // Insert into UserMeal
            try (PreparedStatement stmt = conn.prepareStatement(insertUserMeal)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, mealId);
                stmt.executeUpdate();
            }

            // Insert ingredients
            try (PreparedStatement stmt = conn.prepareStatement(insertMealIngredient)) {
                for (MealIngredient ingredient : ingredients) {
                    stmt.setInt(1, mealId);
                    stmt.setLong(2, ingredient.getFood().getFoodId());
                    stmt.setDouble(3, ingredient.getQuantity());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasMealTypeLogged(int userId, String mealType, Date mealDate) {
        String sql = "SELECT COUNT(*) FROM UserMeal um " +
                     "JOIN Meal m ON um.MealID = m.MealID " +
                     "WHERE um.user_id = ? AND m.MealType = ? AND m.MealDate = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, mealType);
            stmt.setDate(3, new java.sql.Date(mealDate.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getLoggedMealsWithCaloriesForUser(int userId) {
        List<String> meals = new ArrayList<>();
        String sql =
            "SELECT " +
            "    m.MealID, " +
            "    m.meal_name, " +
            "    m.MealDate, " +
            "    m.MealType, " +
            "    ROUND(SUM((IFNULL(na.NutrientValue, 0) / 100) * mi.Qty_grams), 1) AS total_calories " +
            "FROM usermeal um " +
            "JOIN meal m ON um.MealID = m.MealID " +
            "JOIN mealingredient mi ON m.MealID = mi.MealID " +
            "JOIN nutrientamount na ON mi.FoodID = na.FoodID " +
            "JOIN nutrientname nn ON na.NutrientNameID = nn.NutrientNameID " +
            "WHERE um.user_id = ? " +
            "  AND nn.NutrientName LIKE '%ENERGY%'" +
            "GROUP BY m.MealID, m.meal_name, m.MealDate, m.MealType " +
            "ORDER BY m.MealDate DESC, m.MealType";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int mealId = rs.getInt("MealID");
                    String mealName = rs.getString("meal_name");
                    String mealType = rs.getString("MealType");
                    String mealDate = rs.getDate("MealDate").toString();
                    double calories = rs.getDouble("total_calories");

                    String formatted = String.format(
                        "<html><span style='display:none'>%d</span>&#8226; <b>%s</b> (%s) - %s &nbsp;&nbsp;<i>[%.1f kcal]</i></html>",
                        mealId, mealName, mealType, mealDate, calories);
                    meals.add(formatted);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }

    public Map<String, Double> getMacronutrientsForMeal(int userId, int mealId) {
        Map<String, Double> nutrientMap = new HashMap<>();

        String sql =
                "SELECT nn.NutrientSymbol, " +
                "       ROUND(SUM((IFNULL(na.NutrientValue, 0) / 100) * mi.Qty_grams), 2) AS total_amount " +
                "FROM meal m " +
                "JOIN usermeal um ON m.MealID = um.MealID " +
                "JOIN mealingredient mi ON m.MealID = mi.MealID " +
                "JOIN nutrientamount na ON mi.FoodID = na.FoodID " +
                "JOIN nutrientname nn ON na.NutrientNameID = nn.NutrientNameID " +
                "WHERE m.MealID = ? AND um.user_id = ? AND nn.NutrientSymbol IN ('PROT', 'FAT', 'CARB') " +
                "GROUP BY nn.NutrientSymbol";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mealId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nutrient = rs.getString("NutrientSymbol");
                    double amount = rs.getDouble("total_amount");
                    System.out.println("Fetched nutrient: " + nutrient + " = " + amount); // ✅ Debug print
                    nutrientMap.put(nutrient, amount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nutrientMap;
    }


}