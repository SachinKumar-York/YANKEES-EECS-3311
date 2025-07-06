package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    public enum DBType {
        MYSQL,
        POSTGRES
    }

    // Factory method to get connection
    public static Connection getConnection(DBType type) throws SQLException {
        switch (type) {
            case MYSQL:
                return getMySQLConnection();
            case POSTGRES:
                // Placeholder: not implemented yet
                System.out.println("Postgres support coming soon...");
                return null;
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + type);
        }
    }

    // MySQL connection logic
    private static Connection getMySQLConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/cnf";
        String user = "root";
        String password = "Sachin123";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, password);
    }
}