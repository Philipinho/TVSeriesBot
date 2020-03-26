package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB {
    private static Connection connection;
    private static final String DATABASE = ReadProperty.getValue("mysql.db");

    public static void saveUser(String username, String telegramId) {
        String sql = "INSERT INTO tvseries(username,telegram_id) VALUES(?,?)";
        try {
            connection = DriverManager.getConnection(DATABASE);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2,telegramId);
            ps.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
