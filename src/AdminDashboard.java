import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AdminDashboard {
    private Stage stage;
    private App app;
    private TableView<User> userTable;
    private ObservableList<User> userList;
    private TableView<Log> logTable;
    private ObservableList<Log> logList;
    private int userId;

    public AdminDashboard(Stage stage, App app) {
        this.stage = stage;
        this.app = app;
        userList = FXCollections.observableArrayList();
        logList = FXCollections.observableArrayList();
    }

    public VBox getLayout() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Header
        Label header = new Label("Admin Dashboard - Manage Users and View Logs");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // User Table
        userTable = new TableView<>();
        setupUserTable();
        loadUsers(); // Load users from the database

        // Log Table
        logTable = new TableView<>();
        setupLogTable();
        loadLogsFromFile(); // Load logs from log file

        // Add User Section
        VBox addUserBox = createAddUserSection();

        // Layout
        layout.getChildren().addAll(header, userTable, addUserBox, new Label("System Logs"), logTable);
        return layout;
    }

    private void setupUserTable() {
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> data.getValue().idProperty().asObject());

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> data.getValue().usernameProperty());

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> data.getValue().emailProperty());

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> data.getValue().roleProperty());

        TableColumn<User, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user); // Handle user deletion
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        userTable.getColumns().addAll(idCol, usernameCol, emailCol, roleCol, actionCol);
        userTable.setItems(userList);
    }

    private void setupLogTable() {
        TableColumn<Log, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(data -> data.getValue().logIdProperty().asObject());
    
        TableColumn<Log, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(data -> data.getValue().actionProperty());
    
        TableColumn<Log, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(data -> data.getValue().timestampProperty());
    
        logTable.getColumns().addAll(logIdCol, actionCol, timestampCol);
    
        // Load logs from the file
        logList = FXCollections.observableArrayList();
        loadLogsFromFile();
    
        logTable.setItems(logList);
    }

    private VBox createAddUserSection() {
        VBox addUserBox = new VBox(10);
        addUserBox.setPadding(new Insets(10));
        addUserBox.setAlignment(Pos.CENTER_LEFT);

        Label addUserHeader = new Label("Add New User");
        addUserHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("CUSTOMER", "LIBRARIAN", "ADMIN");
        roleComboBox.setValue("CUSTOMER");

        Button addUserButton = new Button("Add User");
        addUserButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addUserButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            addUser(username, email, password, role); // Handle adding a new user
        });

        addUserBox.getChildren().addAll(addUserHeader, usernameField, emailField, passwordField, roleComboBox,
                addUserButton);
        return addUserBox;
    }

    private void loadUsers() {
        userList.clear();
        try (Connection connection = DBUtils.getConnection()) {
            String query = "SELECT user_id, username, email, role FROM users";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");

                User user = new User(id, username, email, role);
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLogsFromFile() {
    String logFile = "src/application_logs.txt";
    try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
        String line;
        int logId = 1;
        while ((line = reader.readLine()) != null) {
            // Each line is expected to follow the format:
            // [TIMESTAMP] UserID: <ID> - <Action>
            if (line.contains("]")) {
                int timestampEnd = line.indexOf("]");
                String timestamp = line.substring(1, timestampEnd);
                String logDetails = line.substring(timestampEnd + 2); // Skip "] "

                // Extract UserID and Action
                int userIdStart = logDetails.indexOf("UserID: ") + 8;
                int userIdEnd = logDetails.indexOf(" - ");
                String action = logDetails.substring(userIdEnd + 3);

                // Create a Log object and add it to the list
                Log log = new Log(logId, action, timestamp);
                logList.add(log);
                logId++;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private void addUser(String username, String email, String password, String role) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            LogUtils.logAction(-1, "Add user failed: Validation error - missing fields");
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        try (Connection connection = DBUtils.getConnection()) {
            String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);

            stmt.executeUpdate();
            LogUtils.logAction(-1, "User added successfully: " + username + ", Role: " + role);
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");
            loadUsers();
        } catch (Exception e) {
            LogUtils.logAction(-1, "Add user failed: Exception - " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user: " + e.getMessage());
        }
    }

    private void deleteUser(User user) {
        try (Connection connection = DBUtils.getConnection()) {
            String query = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, user.getId());

            stmt.executeUpdate();
            LogUtils.logAction(user.getId(), "User deleted successfully: " + user.getUsername());
            showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
            loadUsers();
        } catch (Exception e) {
            LogUtils.logAction(user.getId(), "Delete user failed: Exception - " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
