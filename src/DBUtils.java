import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class DBUtils {
    // Database URL, username, and password
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ssd-project-lms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    // Load the MySQL JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Get a connection to the database
     * 
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static int authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        }
        return -1; // User not found or invalid credentials
    }

    public static String getUserRole(int userId) throws SQLException {
        String query = "SELECT role FROM users WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role");
                }
            }
        }
        return null; // Role not found
    }

    /**
     * Retrieves all books from the database.
     *
     * @return a list of books
     * @throws SQLException if a database error occurs
     */
    public static List<Book> getAllBooks() throws SQLException {
        String query = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
            Book book = new Book(
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("publisher"),
                resultSet.getString("isbn"),
                resultSet.getInt("year_published"),
                resultSet.getString("category"),
                resultSet.getInt("copies_available")
            );
            books.add(book);
            }
        }
        return books;
    }
    
    public static void reserveBook(int userId, int bookId) throws SQLException {
        String query = "INSERT INTO reservations (user_id, book_id, reservation_date, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4, "Pending");

            statement.executeUpdate();
        }
    }

    public static void cancelReservation(int reservationId) throws SQLException {
        String query = "UPDATE reservations SET status = 'Cancelled' WHERE reservation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setInt(1, reservationId);
            statement.executeUpdate();
        }
    }

        /**
     * Retrieves reservations made by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of reservations
     * @throws SQLException if a database error occurs
     */
    public static List<Reservation> getReservationsByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM reservations WHERE user_id = ?";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Reservation reservation = new Reservation(
                            resultSet.getInt("reservation_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getInt("book_id"),
                            resultSet.getTimestamp("reservation_date").toString(),
                            resultSet.getString("status")
                    );
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }

        /**
     * Retrieves all transactions in the database.
     *
     * @return a list of transactions
     * @throws SQLException if a database error occurs
     */
    public static List<Transaction> getAllTransactions() throws SQLException {
        String query = "SELECT * FROM transactions";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
            Transaction transaction = new Transaction(
                resultSet.getInt("transaction_id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("book_id"),
                resultSet.getString("transaction_type"),
                resultSet.getTimestamp("transaction_date").toString(),
                resultSet.getString("due_date"),
                resultSet.getString("return_date"),
                resultSet.getDouble("fine")
            );
            transactions.add(transaction);
            }
        }
        return transactions;
    }

        /**
     * Retrieves a book by its ID.
     *
     * @param bookId the ID of the book
     * @return the book, or null if not found
     * @throws SQLException if a database error occurs
     */
    public static Book getBookById(int bookId) throws SQLException {
        String query = "SELECT * FROM books WHERE book_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Book(
                            resultSet.getInt("book_id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("publisher"),
                            resultSet.getString("isbn"),
                            resultSet.getInt("year_published"),
                            resultSet.getString("category"),
                            resultSet.getInt("copies_available")
                    );
                }
            }
        }
        return null; // Book not found
    }

        /**
     * Retrieves all reservations in the system.
     *
     * @return a list of reservations
     * @throws SQLException if a database error occurs
     */
    public static List<Reservation> getAllReservations() throws SQLException {
        String query = "SELECT * FROM reservations";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
            Reservation reservation = new Reservation(
                resultSet.getInt("reservation_id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("book_id"),
                resultSet.getTimestamp("reservation_date").toString(),
                resultSet.getString("status")
            );
            reservations.add(reservation);
            }
        }
        return reservations;
    }

    /**
     * Close the database connection
     * 
     * @param connection Connection object to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
