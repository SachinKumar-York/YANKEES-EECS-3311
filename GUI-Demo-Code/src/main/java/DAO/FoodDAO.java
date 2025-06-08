package DAO;

import Models.Food;
import Models.MealIngredient;
import DAO.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
}