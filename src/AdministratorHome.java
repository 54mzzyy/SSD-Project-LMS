import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdministratorHome {
    private Stage stage;
    private String username;
    private VBox mainLayout;
    private TableView<Object> contentTable;  // Generic table for displaying various data

    public AdministratorHome(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        // Log admin login for security auditing (SUC_9)
        logAction("Admin login: " + username);
    }

    public void initializeComponents() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #1e1e1e;");

        // Header Section
        Label welcomeLabel = new Label("Welcome, Administrator: " + username);
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");

        // Create main control panel
        GridPane controlPanel = createControlPanel();
        
        // Create content area
        contentTable = new TableView<>();
        contentTable.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        contentTable.setMinHeight(300);

        // Status bar for system messages
        Label statusBar = new Label("System Ready");
        statusBar.setStyle("-fx-text-fill: #90EE90; -fx-background-color: #333333; -fx-padding: 5;");
        statusBar.setMaxWidth(Double.MAX_VALUE);

        mainLayout.getChildren().addAll(welcomeLabel, controlPanel, contentTable, statusBar);
        Scene adminScene = new Scene(mainLayout, 800, 600);
        stage.setScene(adminScene);
        stage.setTitle("Library Management System - Administrator");
    }

    private GridPane createControlPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #333333; -fx-padding: 15;");

        // Member Management Section (UC_5, SUC_5)
        Button manageMembersBtn = createButton("Manage Members", () -> {
            logAction("Accessing member management");
            showMemberManagement();
        });

        // Inventory Management Section (UC_3, SUC_3)
        Button addBookBtn = createButton("Add New Book", () -> {
            logAction("Accessing book addition");
            showAddBook();
        });

        // Report Generation Section (UC_9, SUC_9)
        Button generateReportBtn = createButton("Generate Reports", () -> {
            logAction("Generating inventory report");
            generateInventoryReport();
        });

        // Security Management
        Button viewLogsBtn = createButton("View Audit Logs", () -> {
            logAction("Viewing system logs");
            showAuditLogs();
        });

        Button changePasswordBtn = createButton("Change Password", () -> {
            logAction("Accessing password change");
            new UserChangePassword(stage, username).initializeComponents();
        });

        // Add buttons to grid
        grid.add(manageMembersBtn, 0, 0);
        grid.add(addBookBtn, 1, 0);
        grid.add(generateReportBtn, 0, 1);
        grid.add(viewLogsBtn, 1, 1);
        grid.add(changePasswordBtn, 0, 2);

        return grid;
    }

    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #444444; " +
            "-fx-text-fill: white; " +
            "-fx-min-width: 150; " +
            "-fx-min-height: 40;"
        );
        button.setOnAction(e -> action.run());
        return button;
    }

    // UC_5: Manage Members implementation
    private void showMemberManagement() {
        if (!checkAdminPrivilege()) {
            showError("Insufficient privileges for member management");
            return;
        }
        
        // Implementation would include member CRUD operations
        updateStatus("Accessing member management system...");
    }

    // UC_3: Add New Book implementation
    private void showAddBook() {
        if (!checkAdminPrivilege()) {
            showError("Insufficient privileges for adding books");
            return;
        }
        
        // Implementation would include book addition form
        updateStatus("Accessing book addition system...");
    }

    // UC_9: Generate Inventory Report implementation
    private void generateInventoryReport() {
        if (!checkAdminPrivilege()) {
            showError("Insufficient privileges for generating reports");
            return;
        }
        
        // Implementation would include report generation logic
        updateStatus("Generating inventory report...");
    }

    // Security audit log viewer implementation
    private void showAuditLogs() {
        if (!checkAdminPrivilege()) {
            showError("Insufficient privileges for viewing logs");
            return;
        }
        
        // Implementation would include log viewing interface
        updateStatus("Loading system audit logs...");
    }

    // Security-related helper methods
    private boolean checkAdminPrivilege() {
        // Implementation would include actual privilege checking logic
        return true; // Placeholder
    }

    private void logAction(String action) {
        LocalDateTime timestamp = LocalDateTime.now();
        String logEntry = String.format("[%s] User: %s - Action: %s",
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
}