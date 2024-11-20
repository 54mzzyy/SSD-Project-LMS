import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LibrarianHome {
    private Stage stage;
    private String username;
    private VBox mainLayout;
    private TableView<Object> contentTable;

    public LibrarianHome(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        // Log librarian login for security auditing (SUC_9)
        logAction("Librarian login: " + username);
    }

    public void initializeComponents() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #1e1e1e;");

        // Header Section
        Label welcomeLabel = new Label("Welcome, Librarian: " + username);
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(welcomeLabel, createLogoutButton());

        // Create main control panel
        GridPane controlPanel = createControlPanel();
        
        // Create content area for displaying books, members, etc.
        contentTable = new TableView<>();
        contentTable.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        contentTable.setMinHeight(300);

        // Status bar for system messages
        Label statusBar = new Label("System Ready");
        statusBar.setStyle("-fx-text-fill: #90EE90; -fx-background-color: #333333; -fx-padding: 5;");
        statusBar.setMaxWidth(Double.MAX_VALUE);

        mainLayout.getChildren().addAll(headerBox, controlPanel, contentTable, statusBar);
        Scene librarianScene = new Scene(mainLayout, 800, 600);
        stage.setScene(librarianScene);
        stage.setTitle("Library Management System - Librarian");
    }

    private GridPane createControlPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #333333; -fx-padding: 15;");

        // Book Circulation Section
        VBox circulationBox = createSection("Book Circulation");
        circulationBox.getChildren().addAll(
            createButton("Checkout Book", () -> handleCheckout()), // UC_1
            createButton("Return Book", () -> handleReturn()),     // UC_2
            createButton("Handle Reservations", () -> handleReservations()) // UC_6
        );

        // Book Management Section
        VBox bookManagementBox = createSection("Book Management");
        bookManagementBox.getChildren().addAll(
            createButton("Search Books", () -> handleSearch()), // UC_4
            createButton("Add New Book", () -> handleAddBook()), // UC_3
            createButton("View Inventory", () -> generateInventoryReport()) // UC_9
        );

        // Member Management Section
        VBox memberBox = createSection("Member Services");
        memberBox.getChildren().addAll(
            createButton("Manage Members", () -> handleMemberManagement()), // UC_5
            createButton("View History", () -> viewBorrowingHistory()),     // UC_8
            createButton("Process Notifications", () -> handleNotifications()) // UC_7
        );

        // Account Section
        VBox accountBox = createSection("Account");
        accountBox.getChildren().addAll(
            createButton("Change Password", () -> {
                logAction("Accessing password change");
                new UserChangePassword(stage, username).initializeComponents();
            }),
            createLogoutButton()
        );

        // Add sections to grid
        grid.add(circulationBox, 0, 0);
        grid.add(bookManagementBox, 1, 0);
        grid.add(memberBox, 0, 1);
        grid.add(accountBox, 1, 1);

        return grid;
    }

    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #444444; -fx-padding: 10; -fx-background-radius: 5;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        section.getChildren().add(titleLabel);
        return section;
    }

    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #555555; " +
            "-fx-text-fill: white; " +
            "-fx-min-width: 150;"
        );
        button.setOnAction(e -> {
            if (checkLibrarianPrivilege()) {
                action.run();
            } else {
                showError("Insufficient privileges for this action");
            }
        });
        return button;
    }

    // UC_1: Checkout Book implementation
    private void handleCheckout() {
        logAction("Initiating book checkout");
        validateTransaction(() -> {
            // Implementation would include checkout logic
            updateStatus("Processing book checkout...");
        });
    }

    // UC_2: Return Book implementation
    private void handleReturn() {
        logAction("Initiating book return");
        validateTransaction(() -> {
            // Implementation would include return logic
            updateStatus("Processing book return...");
        });
    }

    // UC_3: Add New Book implementation
    private void handleAddBook() {
        logAction("Accessing book addition");
        validateTransaction(() -> {
            // Implementation would include book addition form
            updateStatus("Adding new book to catalog...");
        });
    }

    // UC_4: Search Books implementation
    private void handleSearch() {
        logAction("Accessing book search");
        // Implementation would include search interface
        updateStatus("Searching books...");
    }

    // UC_5: Manage Members implementation
    private void handleMemberManagement() {
        logAction("Accessing member management");
        validateTransaction(() -> {
            // Implementation would include member management interface
            updateStatus("Managing member information...");
        });
    }

    // UC_6: Handle Reservations implementation
    private void handleReservations() {
        logAction("Accessing reservation management");
        validateTransaction(() -> {
            // Implementation would include reservation management
            updateStatus("Processing reservations...");
        });
    }

    // UC_7: Handle Notifications implementation
    private void handleNotifications() {
        logAction("Processing notifications");
        validateTransaction(() -> {
            // Implementation would include notification management
            updateStatus("Managing notifications...");
        });
    }

    // UC_8: View Borrowing History implementation
    private void viewBorrowingHistory() {
        logAction("Accessing borrowing history");
        validateTransaction(() -> {
            // Implementation would include history viewing interface
            updateStatus("Viewing borrowing history...");
        });
    }

    // UC_9: Generate Inventory Report implementation
    private void generateInventoryReport() {
        logAction("Generating inventory report");
        validateTransaction(() -> {
            // Implementation would include report generation
            updateStatus("Generating inventory report...");
        });
    }

    // Security-related helper methods
    private boolean checkLibrarianPrivilege() {
        // Implementation would include actual privilege checking logic
        return true; // Placeholder
    }

    private void validateTransaction(Runnable action) {
        if (checkLibrarianPrivilege()) {
            action.run();
        } else {
            showError("Authorization failed");
        }
    }

    private void logAction(String action) {
        LocalDateTime timestamp = LocalDateTime.now();
        String logEntry = String.format("[%s] Librarian: %s - Action: %s",
            timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            username,
            action
        );
        // Implementation would include actual logging logic
        System.out.println(logEntry);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateStatus(String message) {
        Label statusBar = (Label) mainLayout.getChildren().get(mainLayout.getChildren().size() - 1);
        statusBar.setText(message);
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