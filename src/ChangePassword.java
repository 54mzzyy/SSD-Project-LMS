import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePassword {
    private Stage stage;
    private App app;
    private String username;

    public ChangePassword(Stage stage, App app, String username) {
        this.stage = stage;
        this.app = app;
        this.username = username;
    }

    public VBox getLayout() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label changePasswordLabel = new Label("Change Password");
        changePasswordLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Enter Old Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        changePasswordButton.setOnAction(e -> handleChangePassword(
                oldPasswordField.getText(),
                newPasswordField.getText(),
                confirmPasswordField.getText()
        ));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> handleBackButton());

        layout.getChildren().addAll(
                changePasswordLabel,
                oldPasswordField,
                newPasswordField,
                confirmPasswordField,
                changePasswordButton,
                backButton
        );

        return layout;
    }

    private void handleChangePassword(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "New password and confirm password do not match.");
            return;
        }

        try (Connection connection = DBUtils.getConnection()) {
            // Validate old password
            String validateQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement validateStatement = connection.prepareStatement(validateQuery);
            validateStatement.setString(1, username);
            validateStatement.setString(2, oldPassword);

            ResultSet resultSet = validateStatement.executeQuery();

            if (!resultSet.next()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Old Password", "The old password you entered is incorrect.");
                LogUtils.logAction(-1, "Password change failed: Invalid old password for user " + username);
                return;
            }

            // Update the password in the database
            String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, newPassword);
            updateStatement.setString(2, username);

            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Password Changed", "Your password has been updated successfully.");
                LogUtils.logAction(-1, "Password changed successfully for user " + username);
                app.showLoginScreen(); // Redirect to login screen after success
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating your password.");
                LogUtils.logAction(-1, "Password change failed: Database update error for user " + username);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred: " + e.getMessage());
            LogUtils.logAction(-1, "Password change failed: Database error for user " + username + " - " + e.getMessage());
        }
    }

    private void handleBackButton() {
        try {
            int userId = DBUtils.getUserIdByUsername(username);
            if (userId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "User not found.");
                LogUtils.logAction(userId, "Back button pressed: User not found for username " + username);
                return;
            }

            String role = DBUtils.getUserRole(userId);

            if (role == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "User role not found.");
                LogUtils.logAction(userId, "Back button pressed: User role not found for user " + username);
                return;
            }

            switch (role.toUpperCase()) {
                case "CUSTOMER":
                    app.showCustomerDashboard(username, userId);
                    break;
                case "LIBRARIAN":
                    app.showLibrarianDashboard(username, userId);
                    break;
                case "ADMIN":
                    app.showAdministratorDashboard(username, userId);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid role assigned to the user.");
                    LogUtils.logAction(userId, "Back button pressed: Invalid role for user " + username);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred: " + e.getMessage());
            LogUtils.logAction(-1, "Back button pressed: Database error for user " + username + " - " + e.getMessage());
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
