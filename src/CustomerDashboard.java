import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class CustomerDashboard {
    private VBox layout;

    public CustomerDashboard(Stage stage, App app, String username) {
        layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Customer Dashboard");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label welcomeLabel = new Label("Welcome, " + username);

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> app.showChangePasswordScreen(username));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> app.showLoginScreen());

        layout.getChildren().addAll(titleLabel, welcomeLabel, changePasswordButton, logoutButton);
    }

    public VBox getLayout() {
        return layout;
    }
}
