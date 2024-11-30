import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;

public class SignUp {
    private Stage stage;
    private App app;

    public SignUp(Stage stage, App app) {
        this.stage = stage;
        this.app = app;
    }

    public VBox getLayout() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(300);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        signUpButton.setOnAction(e -> handleSignUp(
                usernameField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                emailField.getText()

        ));

        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> app.showLoginScreen());

        layout.getChildren().addAll(
                signUpLabel,
                usernameField,
                passwordField,
                confirmPasswordField,
                emailField,
                signUpButton,
                backButton);

        return layout;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private void handleSignUp(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            LogUtils.logAction(-1, "Sign-up failed: Validation error - missing fields");
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            LogUtils.logAction(-1, "Sign-up failed: Validation error - passwords do not match");
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return;
        }

        if (!isValidEmail(email)) {
            LogUtils.logAction(-1, "Sign-up failed: Validation error - invalid email format");
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid email format.");
            return;
        }

        if (!PasswordUtils.isPasswordValid(password)) {
            LogUtils.logAction(-1, "Sign-up failed: Validation error - password does not meet policy");
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character.");
            return;
        }

        try (Connection connection = DBUtils.getConnection()) {
            String checkUserQuery = "SELECT COUNT(*) AS user_count FROM users WHERE username = ? OR email = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
            checkUserStmt.setString(1, username);
            checkUserStmt.setString(2, email);

            var resultSet = checkUserStmt.executeQuery();
            if (resultSet.next()) {
                int userCount = resultSet.getInt("user_count");
                if (userCount > 0) {
                    LogUtils.logAction(-1, "Sign-up failed: Username or email already taken - " + username);
                    showAlert(Alert.AlertType.ERROR, "User Exists", "Username or email is already taken.");
                    return;
                }
            }

            String salt = PasswordUtils.generateSalt();
            String hashedPassword = PasswordUtils.hashPassword(password, salt);

            String insertUserQuery = "INSERT INTO users (username, password, pass_salt, email, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertUserStmt = connection.prepareStatement(insertUserQuery);
            insertUserStmt.setString(1, username);
            insertUserStmt.setString(2, hashedPassword);
            insertUserStmt.setString(3, salt);
            insertUserStmt.setString(4, email);
            insertUserStmt.setString(5, "CUSTOMER");

            int rowsInserted = insertUserStmt.executeUpdate();
            if (rowsInserted > 0) {
                LogUtils.logAction(-1, "Sign-up successful for username: " + username);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Sign-up successful! You can now log in.");
                app.showLoginScreen();
            } else {
                LogUtils.logAction(-1, "Sign-up failed: Database error for username: " + username);
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during sign-up.");
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            LogUtils.logAction(-1, "Sign-up failed: Exception - " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred: " + e.getMessage());
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
