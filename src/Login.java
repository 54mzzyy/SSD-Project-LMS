import javafx.geometry.Insets;
import javafx.geometry.Pos;
//import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    private Stage stage;
    private App app;

    public Login(Stage stage, App app) {
        this.stage = stage;
        this.app = app;
    }

    public VBox getLayout() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Login");
        loginLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));

        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        signUpButton.setOnAction(e -> app.showSignUpScreen());

        layout.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton, signUpButton);

        return layout;
    }

    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            LogUtils.logAction(-1, "Login failed: Missing username or password");
            return;
        }
    
        try {
            // Authenticate user using the DBUtils class
            int userId = DBUtils.authenticateUser(username, password);
    
            if (userId != -1) {
                // Fetch user role from DB
                String role = DBUtils.getUserRole(userId);
    
                // Log successful login
                LogUtils.logAction(userId, "Successful login - Role: " + role);
    
                switch (role.toUpperCase()) {
                    case "CUSTOMER":
                        app.showCustomerDashboard(username);
                        break;
                    case "LIBRARIAN":
                        app.showLibrarianDashboard(username);
                        break;
                    case "ADMIN":
                        app.showAdministratorDashboard(username);
                        break;
                    default:
                        showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid role assigned to the user.");
                        LogUtils.logAction(userId, "Login failed: Invalid role");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                LogUtils.logAction(-1, "Login failed: Invalid username or password");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while connecting to the database: " + e.getMessage());
            LogUtils.logAction(-1, "Login failed: Database error - " + e.getMessage());
        }
    }
    
    

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
