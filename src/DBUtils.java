import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtils {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ssd-project-lms"; // Database URL
    private static final String DB_USER = "root"; // Database username
    private static final String DB_PASSWORD = "1234"; // Database password

    /**
     * Establishes and returns a connection to the database.
     *
     * @return Connection object
     * @throws Exception if database connection fails
     */
    public static Connection establishConnection() throws Exception {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to connect to the database.");
        }
    }

    /**
     * Executes a SELECT query and returns the ResultSet.
     *
     * @param query SQL SELECT query
     * @param parameters Query parameters (if any)
     * @return ResultSet containing query results
     * @throws Exception if query execution fails
     */
    public static ResultSet executeQuery(String query, Object... parameters) throws Exception {
        Connection connection = establishConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        setParameters(statement, parameters);
        return statement.executeQuery();
    }

    /**
     * Executes an INSERT, UPDATE, or DELETE query.
     *
     * @param query SQL query
     * @param parameters Query parameters (if any)
     * @return Number of rows affected
     * @throws Exception if query execution fails
     */
    public static int executeUpdate(String query, Object... parameters) throws Exception {
        try (Connection connection = establishConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setParameters(statement, parameters);
            return statement.executeUpdate();
        }
    }

    /**
     * Helper method to set query parameters for PreparedStatement.
     *
     * @param statement PreparedStatement object
     * @param parameters Query parameters
     * @throws Exception if parameter setting fails
     */
    private static void setParameters(PreparedStatement statement, Object... parameters) throws Exception {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }

    /**
     * Closes database resources to prevent memory leaks.
     *
     * @param connection Connection object
     * @param statement Statement object
     * @param resultSet ResultSet object
     */
    public static void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
