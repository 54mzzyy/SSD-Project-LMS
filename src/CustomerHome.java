import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerHome {
    private Stage stage;
    private String username;
    private TableView<Book> searchResultsTable;
    private TableView<BorrowingRecord> historyTable;
    private VBox mainLayout;

    public CustomerHome(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        // Add logging for security audit
        logAction("Customer login: " + username);
    }

    public void initializeComponents() {
        // Create main layout
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #1e1e1e;");

        // Welcome header with consistent styling
        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(welcomeLabel, createLogoutButton());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create tabs with consistent styling
        Tab searchTab = createSearchBooksTab();
        Tab historyTab = createBorrowingHistoryTab();
        Tab reservationTab = createReservationTab();
        tabPane.getTabs().addAll(searchTab, historyTab, reservationTab);

        // User Settings with consistent styling
        VBox settingsBox = createSettingsSection();

        // Status bar for system messages
        Label statusBar = new Label("System Ready");
        statusBar.setStyle("-fx-text-fill: #90EE90; -fx-background-color: #333333; -fx-padding: 5;");
        statusBar.setMaxWidth(Double.MAX_VALUE);

        mainLayout.getChildren().addAll(headerBox, tabPane, settingsBox, statusBar);

        Scene customerScene = new Scene(mainLayout, 800, 600);
        stage.setScene(customerScene);
        stage.setTitle("Library Management System - Customer Portal");
    }

    private void logAction(String action) {
        LocalDateTime timestamp = LocalDateTime.now();
        String logEntry = String.format("[%s] User: %s - Action: %s",
            timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            username,
            action
        );
        // Implement actual logging logic here
        System.out.println(logEntry);
    }

    // Add updateStatus method for consistency
    private void updateStatus(String message) {
        Label statusBar = (Label) mainLayout.getChildren().get(mainLayout.getChildren().size() - 1);
        statusBar.setText(message);
    }

    private Tab createSearchBooksTab() {
        Tab searchTab = new Tab("Search Books");
        
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(10));
        
        // Search controls
        HBox searchControls = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Enter title, author, or ISBN");
        searchField.setPrefWidth(300);
        
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("Title", "Author", "ISBN");
        searchType.setValue("Title");
        
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        searchControls.getChildren().addAll(searchField, searchType, searchButton);
        
        // Search results table
        searchResultsTable = new TableView<>();
        setupSearchResultsTable();
        
        searchBox.getChildren().addAll(searchControls, searchResultsTable);
        searchTab.setContent(searchBox);
        
        return searchTab;
    }

    private Tab createBorrowingHistoryTab() {
        Tab historyTab = new Tab("Borrowing History");
        
        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(10));
        
        historyTable = new TableView<>();
        setupBorrowingHistoryTable();
        
        historyBox.getChildren().add(historyTable);
        historyTab.setContent(historyBox);
        
        return historyTab;
    }

    private Tab createReservationTab() {
        Tab reservationTab = new Tab("Book Reservations");
        
        VBox reservationBox = new VBox(10);
        reservationBox.setPadding(new Insets(10));
        
        // Current reservations table
        TableView<Reservation> reservationsTable = new TableView<>();
        setupReservationsTable(reservationsTable);
        
        // Reserve book section
        VBox reserveBox = new VBox(10);
        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Enter Book ID");
        
        Button reserveButton = new Button("Reserve Book");
        reserveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        reserveButton.setOnAction(e -> handleReserveBook(bookIdField.getText()));
        
        reserveBox.getChildren().addAll(new Label("Reserve a Book"), bookIdField, reserveButton);
        
        reservationBox.getChildren().addAll(reserveBox, new Label("Current Reservations"), reservationsTable);
        reservationTab.setContent(reservationBox);
        
        return reservationTab;
    }

    private VBox createSettingsSection() {
        VBox settingsBox = new VBox(10);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setPadding(new Insets(10));
        
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        changePasswordButton.setOnAction(e -> {
            UserChangePassword changePasswordPage = new UserChangePassword(stage, username);
            changePasswordPage.initializeComponents();
        });
        
        settingsBox.getChildren().addAll(changePasswordButton);
        
        return settingsBox;
    }

    private void setupSearchResultsTable() {
        // Configure columns for search results
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        
        searchResultsTable.getColumns().addAll(titleCol, authorCol, isbnCol, statusCol);
    }

    private void setupBorrowingHistoryTable() {
        // Configure columns for borrowing history
        TableColumn<BorrowingRecord, String> bookCol = new TableColumn<>("Book");
        TableColumn<BorrowingRecord, LocalDate> checkoutCol = new TableColumn<>("Checkout Date");
        TableColumn<BorrowingRecord, LocalDate> dueCol = new TableColumn<>("Due Date");
        TableColumn<BorrowingRecord, LocalDate> returnCol = new TableColumn<>("Return Date");
        
        historyTable.getColumns().addAll(bookCol, checkoutCol, dueCol, returnCol);
    }

    private void setupReservationsTable(TableView<Reservation> table) {
        // Configure columns for reservations
        TableColumn<Reservation, String> bookCol = new TableColumn<>("Book");
        TableColumn<Reservation, LocalDate> dateCol = new TableColumn<>("Reservation Date");
        TableColumn<Reservation, String> statusCol = new TableColumn<>("Status");
        
        table.getColumns().addAll(bookCol, dateCol, statusCol);
    }

    private void handleReserveBook(String bookId) {
        // Implementation for reserving a book (UC_6)
        try {
            // Add reservation logic here
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book reserved successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to reserve book: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Button createLogoutButton() {
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> handleLogout());
        return logoutButton;
    }

    private void handleLogout() {
        logAction("User logout: " + username);
        // Clear any sensitive data
        username = null;
        // Return to login screen
        new UserLogin(stage).initializeComponents();
    }
}

// Supporting classes (would be in separate files in actual implementation)
class Book {
    private String title;
    private String author;
    private String isbn;
    private String status;
    // Add getters and setters
}

class BorrowingRecord {
    private String bookTitle;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    // Add getters and setters
}

class Reservation {
    private String bookTitle;
    private LocalDate reservationDate;
    private String status;
    // Add getters and setters
}