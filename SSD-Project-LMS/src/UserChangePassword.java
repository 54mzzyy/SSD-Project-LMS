import java.sql.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserChangePassword {
    private Scene changePasswordScene;
    private PasswordField newPasswordField = new PasswordField();
    private Stage stage;
    private String username;

    public UserChangePassword(Stage primaryStage, String username) {
        this.stage = primaryStage;
        this.username = username;
    }

    public void initializeComponents() {
        VBox changePasswordLayout = new VBox(10);
        changePasswordLayout.setPadding(new Insets(10));
        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // changePassword();
                changePassword();
            }
        });
        changePasswordLayout.getChildren().addAll(new Label("Welcome " + username), new Label("New Password:"),
                newPasswordField, changePasswordButton);

        changePasswordScene = new Scene(changePasswordLayout, 300, 200);
        stage.setTitle("Change Password");
        stage.setScene(changePasswordScene);
        stage.show();
    }

    private void changePassword() {
        String newPassword = newPasswordField.getText();

        // Check if the new password is empty
        if (newPassword.isEmpty()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return; // Exit the method if the password is empty
        }

        Connection con = DBUtils.establishConnection();
        String query = "UPDATE users SET password=? WHERE username=?;";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            // Using prepared statement to prevent SQL injection
            statement.setString(1, newPassword);
            statement.setString(2, username);

            int result = statement.executeUpdate();

            if (result == 1) {
                showAlert("Success", "Password successfully changed");
            } else {
                showAlert("Failure", "Failed to update password");
            }

            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
