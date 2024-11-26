import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public static void completeReservation(int reservationId) throws SQLException {
        String updateReservationQuery = "UPDATE reservations SET status = 'Completed' WHERE reservation_id = ?";
        String updateBookQuery = "UPDATE books SET copies_available = copies_available - 1 WHERE book_id = (SELECT book_id FROM reservations WHERE reservation_id = ?)";

        try (Connection connection = getConnection();
             PreparedStatement updateReservationStmt = connection.prepareStatement(updateReservationQuery);
             PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {

            connection.setAutoCommit(false);

            updateReservationStmt.setInt(1, reservationId);
            updateReservationStmt.executeUpdate();

            updateBookStmt.setInt(1, reservationId);
            updateBookStmt.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try (Connection connection = getConnection()) {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            throw e;
        }
    }

    public static void cancelReservation(int reservationId) throws SQLException {
        String updateReservationQuery = "UPDATE reservations SET status = 'Cancelled' WHERE reservation_id = ?";
        String updateBookQuery = "UPDATE books SET copies_available = copies_available + 1 WHERE book_id = (SELECT book_id FROM reservations WHERE reservation_id = ?)";

        try (Connection connection = getConnection();
             PreparedStatement updateReservationStmt = connection.prepareStatement(updateReservationQuery);
             PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {

            connection.setAutoCommit(false);

            updateReservationStmt.setInt(1, reservationId);
            updateReservationStmt.executeUpdate();

            updateBookStmt.setInt(1, reservationId);
            updateBookStmt.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try (Connection connection = getConnection()) {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            throw e;
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
        String query = "SELECT r.*, b.title, b.author, b.category FROM reservations r JOIN books b ON r.book_id = b.book_id WHERE r.user_id = ?";
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
                            resultSet.getString("status"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("category")
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
        String query = "SELECT r.*, b.title, b.author, b.category FROM reservations r JOIN books b ON r.book_id = b.book_id";
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
                resultSet.getString("status"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("category")
            );
            reservations.add(reservation);
            }
        }
        return reservations;
    }

    /**
     * Searches for books based on a search text.
     *
     * @param searchText the text to search for
     * @return a list of books that match the search criteria
     * @throws SQLException if a database error occurs
     */
    public static List<Book> searchBooks(String searchText) throws SQLException {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR publisher LIKE ? OR year_published LIKE ? OR category LIKE ?";
        List<Book> books = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
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
        }
        return books;
    }

    public static List<Book> searchBooks(String userId, String searchText) throws SQLException {
        RateLimiter rateLimiter = new RateLimiter(5, TimeUnit.MINUTES.toMillis(1)); // Allow 5 requests per minute

        if (!rateLimiter.isAllowed(userId)) {
            throw new SQLException("Rate limit exceeded. Please try again later.");
        }

        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR publisher LIKE ? OR year_published LIKE ? OR category LIKE ?";
        List<Book> books = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
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
        }
        return books;
    }

    public static void borrowBook(int userId, int bookId) throws SQLException {
        String query = "INSERT INTO transactions (user_id, book_id, transaction_type, transaction_date, due_date, fine) VALUES (?, ?, 'Borrow', ?, ?, 0)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4, LocalDateTime.now().plusDays(14).toString()); // Assuming a 2-week borrowing period

            statement.executeUpdate();
        }
    }

    public static void returnBook(int userId, int bookId) throws SQLException {
        String query = "INSERT INTO transactions (user_id, book_id, transaction_type, transaction_date, return_date, fine) VALUES (?, ?, 'Return', ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4, LocalDateTime.now().toString());
            statement.setDouble(5, calculateFine(userId, bookId)); // Assuming a method to calculate fine

            statement.executeUpdate();
        }
    }

    private static double calculateFine(int userId, int bookId) throws SQLException {
        // Implement fine calculation logic here
        return 0.0;
    }

    public static void addBook(Book book) throws SQLException {
        String query = "INSERT INTO books (title, author, publisher, isbn, year_published, category, copies_available) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getPublisher());
            statement.setString(4, book.getIsbn());
            statement.setInt(5, book.getYearPublished());
            statement.setString(6, book.getCategory());
            statement.setInt(7, book.getCopiesAvailable());

            statement.executeUpdate();
        }
    }

    public static void updateBook(Book book) throws SQLException {
        String query = "UPDATE books SET title = ?, author = ?, publisher = ?, isbn = ?, year_published = ?, category = ?, copies_available = ? WHERE book_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getPublisher());
            statement.setString(4, book.getIsbn());
            statement.setInt(5, book.getYearPublished());
            statement.setString(6, book.getCategory());
            statement.setInt(7, book.getCopiesAvailable());
            statement.setInt(8, book.getBookId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book updated successfully.");
            } else {
                System.out.println("No book found with the given ID.");
            }
        }
    }

    public static void deleteBook(int bookId) throws SQLException {
        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookId);
            statement.executeUpdate();
        }
    }

    public static int getUserIdByUsername(String username) throws SQLException {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        }
        return -1; // User not found
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
