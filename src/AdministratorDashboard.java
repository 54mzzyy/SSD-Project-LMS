import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class AdministratorDashboard {
    private VBox layout;

    public AdministratorDashboard(Stage stage, App app, String username) {
        layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Administrator Dashboard");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label welcomeLabel = new Label("Welcome, " + username);

        Button manageUsersButton = new Button("Manage Users");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> app.showChangePasswordScreen(username));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> app.showLoginScreen());

        layout.getChildren().addAll(titleLabel, welcomeLabel, manageUsersButton, changePasswordButton, logoutButton);
    }

    public VBox getLayout() {
        return layout;
    }
}
