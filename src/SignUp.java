import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                backButton
        );

        return layout;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    

    private void handleSignUp(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
            return;
        }
    
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return;
        }
    
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid email format.");
            return;
        }
    
        try (Connection connection = DBUtils.getConnection()) {
            // Check if username or email already exists
            String checkUserQuery = "SELECT COUNT(*) AS user_count FROM users WHERE username = ? OR email = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
            checkUserStmt.setString(1, username);
            checkUserStmt.setString(2, email);
    
            var resultSet = checkUserStmt.executeQuery();
            if (resultSet.next()) {
                int userCount = resultSet.getInt("user_count");
                if (userCount > 0) {
                    showAlert(Alert.AlertType.ERROR, "User Exists", "Username or email is already taken.");
                    return;
                }
            }
    
            // Insert the new user as a Customer
            String insertUserQuery = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            PreparedStatement insertUserStmt = connection.prepareStatement(insertUserQuery);
            insertUserStmt.setString(1, username);
            insertUserStmt.setString(2, password);
            insertUserStmt.setString(3, email);
            insertUserStmt.setString(4, "CUSTOMER");
    
            int rowsInserted = insertUserStmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Sign-up successful! You can now log in.");
                app.showLoginScreen(); // Redirect to login screen after successful sign-up
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during sign-up.");
            }
        } catch (SQLException e) {
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
