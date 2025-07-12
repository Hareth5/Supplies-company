import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234";
    private static final String URL = "127.0.0.1";
    private static final String PORT = "3305";
    private static final String DB_NAME = "asha";
    private static final String CONNECTION_URL = "jdbc:mysql://" + URL + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    public static Connection connect() throws SQLException {
        try {
            return DriverManager.getConnection(CONNECTION_URL, DB_USERNAME, DB_PASSWORD);

        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
}