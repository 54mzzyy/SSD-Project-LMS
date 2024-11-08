import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class Home {
    private Stage stage;
    private String role;
    private String username;

    public Home(Stage stage, String role, String username) {
        this.stage = stage;
        this.role = role;
        this.username = username;
    }

    public void initializeComponents() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #1e1e1e;");

        Label welcomeLabel = new Label("Welcome, " + username + " (" + role + ")");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18;");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        changePasswordButton.setOnAction(e -> {
            UserChangePassword changePasswordPage = new UserChangePassword(stage, username);
            changePasswordPage.initializeComponents();
        });

        layout.getChildren().addAll(welcomeLabel, changePasswordButton);
        Scene homeScene = new Scene(layout, 400, 300);
        stage.setScene(homeScene);
    }
}