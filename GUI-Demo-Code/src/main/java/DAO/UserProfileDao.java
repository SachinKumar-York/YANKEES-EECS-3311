package DAO;
import java.util.*;
import Models.UserProfile;
import java.sql.*;


public class UserProfileDao {

	public int insertUserProfile(UserProfile userProfile) {
	    String sql = "INSERT INTO user (name, email, sex, dob, height_in_meter, weight_in_KG, units) VALUES (?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = DBConnector.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, userProfile.getName());
	        stmt.setString(2, userProfile.getEmail());
	        stmt.setString(3, userProfile.getSex());
	        stmt.setDate(4, new java.sql.Date(userProfile.getDob().getTime()));
	        stmt.setFloat(5, userProfile.getHeight());
	        stmt.setFloat(6, userProfile.getWeight());
	        stmt.setString(7, userProfile.getUnits());

	        int affectedRows = stmt.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("Creating user profile failed, no rows affected.");
	        }

	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Creating user profile failed, no ID obtained.");
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    }
	}


    public List<UserProfile> getAllUserProfiles() {
        List<UserProfile> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UserProfile user = new UserProfile(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("sex"),
                    rs.getDate("dob"),
                    rs.getFloat("height_in_meter"),
                    rs.getFloat("weight_in_KG"),
                    rs.getString("units")
                );
                user.setUserId(rs.getInt("user_id"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }



    public boolean updateUserProfile(int userId, float newHeight, float newWeight, String newUnits) {
        String sql = "UPDATE user SET height_in_meter = ?, weight_in_KG = ?, units = ? WHERE user_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, newHeight);
            stmt.setFloat(2, newWeight);
            stmt.setString(3, newUnits);
            stmt.setInt(4, userId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


