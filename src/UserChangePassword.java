import java.sql.*;
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
        changePasswordLayout.setPadding(new Insets(20));
        changePasswordLayout.setStyle("-fx-background-color: #1e1e1e;");

        Label welcomeLabel = new Label("Change Password for " + username);
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        changePasswordButton.setOnAction(event -> changePassword());

        changePasswordLayout.getChildren().addAll(welcomeLabel, newPasswordField, changePasswordButton);
        changePasswordScene = new Scene(changePasswordLayout, 300, 200);
        stage.setTitle("Change Password");
        stage.setScene(changePasswordScene);
        stage.show();
    }

    private void changePassword() {
        String newPassword = newPasswordField.getText();

        if (newPassword.isEmpty()) {
            showAlert("Validation Error", "Password cannot be empty.");
            return;
        }

        String query = "UPDATE users SET password=? WHERE user_name=?;";
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, newPassword);
            statement.setString(2, username);

            int result = statement.executeUpdate();

            if (result == 1) {
                showAlert("Success", "Password successfully changed");
                Home homePage = new Home(stage, "Customer", username); // Adjust role if needed
                homePage.initializeComponents();
            } else {
                showAlert("Failure", "Failed to update password.");
            }
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
