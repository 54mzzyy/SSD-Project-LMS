import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.SwingWorker;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class AdminDashboard {
    private Stage stage;
    private App app;
    private TableView<User> userTable;
    private ObservableList<User> userList;
    private TableView<Log> logTable;
    private ObservableList<Log> logList;
    private int userId;
    private String username;

    public AdminDashboard(Stage stage, App app, String username, int userId) {
        this.stage = stage;
        this.app = app;
        this.username = username;
        this.userId = userId;
        userList = FXCollections.observableArrayList();
        logList = FXCollections.observableArrayList();
    }

    public void showDashboard() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Button manageUsersButton = new Button("Manage Users");
        manageUsersButton.setOnAction(e -> showManageUsersScreen());

        Button viewLogsButton = new Button("View Logs");
        viewLogsButton.setOnAction(e -> showLogsScreen());

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> app.showChangePasswordScreen(username));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            LogUtils.logAction(userId, "User logged out");
            app.showLoginScreen();
        });

        layout.getChildren().addAll(titleLabel, manageUsersButton, viewLogsButton, changePasswordButton, logoutButton);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void showManageUsersScreen() {
        Stage userStage = new Stage();
        userStage.setTitle("Manage Users");

        userTable = new TableView<>();
        userTable.setItems(getAllUsers());

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
                    deleteUser(user);
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

        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> showAddUserDialog());

        VBox userLayout = new VBox(10, userTable, addUserButton);
        userLayout.setPadding(new Insets(10));

        Scene scene = new Scene(userLayout, 800, 600);
        userStage.setScene(scene);
        userStage.show();
    }

    private void showLogsScreen() {
        Stage logStage = new Stage();
        logStage.setTitle("View Logs");

        logTable = new TableView<>();
        logTable.setItems(getAllLogs());

        TableColumn<Log, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(data -> data.getValue().logIdProperty().asObject());

        TableColumn<Log, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(data -> data.getValue().actionProperty());

        TableColumn<Log, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(data -> data.getValue().timestampProperty());

        logTable.getColumns().addAll(logIdCol, actionCol, timestampCol);

        VBox logLayout = new VBox(10, logTable);
        logLayout.setPadding(new Insets(10));

        Scene scene = new Scene(logLayout, 800, 600);
        logStage.setScene(scene);
        logStage.show();
    }

    private ObservableList<User> getAllUsers() {
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
        return userList;
    }

    private ObservableList<Log> getAllLogs() {
        logList.clear();
        new SwingWorker<List<Log>, Void>() {
            @Override
            protected List<Log> doInBackground() throws Exception {
                List<Log> logs = new ArrayList<>();
                String logFile = "./application_logs.txt";
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    int logId = 1;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("]")) {
                            int timestampEnd = line.indexOf("]");
                            String timestamp = line.substring(1, timestampEnd);
                            String logDetails = line.substring(timestampEnd + 2);

                            int userIdStart = logDetails.indexOf("UserID: ") + 8;
                            int userIdEnd = logDetails.indexOf(" - ");
                            String action = logDetails.substring(userIdEnd + 3);

                            Log log = new Log(logId, action, timestamp);
                            logs.add(log);
                            logId++;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return logs;
            }

            @Override
            protected void done() {
                try {
                    logList.addAll(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
        return logList;
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("CUSTOMER", "LIBRARIAN", "ADMIN");
        roleComboBox.setValue("CUSTOMER");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new User(0, usernameField.getText(), emailField.getText(), roleComboBox.getValue());
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                addUser(user.getUsername(), user.getEmail(), passwordField.getText(), user.getRole());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
            userTable.setItems(getAllUsers());
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
            userTable.setItems(getAllUsers());
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
