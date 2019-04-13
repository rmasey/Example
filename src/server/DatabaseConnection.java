package server;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection connection = null;

    public static void open(String dbFile) {
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);    //need these to cascade delete/update in database
            config.enableLoadExtension(true);  //can use JSON extension (but not really useful as must return ResultSet...
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:resources/" + dbFile, config.toProperties());
            System.out.println("Database connection successfully established.");
        } catch (ClassNotFoundException exception) {
            System.out.println("Class not found exception: " + exception.getMessage());
        } catch (SQLException exception) {
            System.out.println("Database connection error: " + exception.getMessage());
        }
    }

    public static PreparedStatement newStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public static void close() {
        System.out.println("Disconnecting from database.");
        try {
            if (connection != null) connection.close();
        } catch (SQLException exception) {
            System.out.println("Database disconnection error: " + exception.getMessage());
        }
    }

}


