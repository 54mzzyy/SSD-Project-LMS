import java.sql.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class UserLogin {
    private Scene loginScene;
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Stage stage;

    public UserLogin(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label("Library Management System");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        Label usernameLabel = new Label("Username:");
        usernameLabel.setTextFill(Color.LIGHTGRAY);
        usernameField.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.LIGHTGRAY);
        passwordField.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        
        Button loginButton = new Button("Sign In");
        styleButton(loginButton);
        loginButton.setOnAction(event -> authenticate());

        Label orLabel = new Label("or");
        orLabel.setTextFill(Color.LIGHTGRAY);

        Button signUpButton = new Button("Sign Up");
        styleButton(signUpButton);
        signUpButton.setOnAction(e -> showSignUpScene());

        loginLayout.getChildren().addAll(titleLabel, usernameLabel, usernameField,
                                         passwordLabel, passwordField,
                                         loginButton, orLabel, signUpButton);

        loginScene = new Scene(loginLayout, 350, 300);
        stage.setTitle("User Login");
        stage.setScene(loginScene);
        stage.show();
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        String query = "SELECT * FROM users WHERE user_name=? AND password=?;";
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                showAlert("Success", "Login successful.");
                // Proceed to next screen or functionality
            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
    }

    private void showSignUpScene() {
        VBox signUpLayout = new VBox(10);
        signUpLayout.setPadding(new Insets(20));
        signUpLayout.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Label usernameLabel = new Label("Username:");
        usernameLabel.setTextFill(Color.LIGHTGRAY);
        TextField newUsername = new TextField();
        newUsername.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Label emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.LIGHTGRAY);
        TextField emailField = new TextField();
        emailField.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.LIGHTGRAY);
        PasswordField newPassword = new PasswordField();
        newPassword.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        Button createAccountButton = new Button("Create Account");
        styleButton(createAccountButton);
        createAccountButton.setOnAction(e -> {
            createNewUser(newUsername.getText(), emailField.getText(), newPassword.getText());
        });

        signUpLayout.getChildren().addAll(usernameLabel, newUsername,
                                          emailLabel, emailField,
                                          passwordLabel, newPassword,
                                          createAccountButton);

        Scene signUpScene = new Scene(signUpLayout, 350, 350);
        stage.setScene(signUpScene);
    }

    private void createNewUser(String username, String email, String password) {
        String query = "INSERT INTO users (user_name, email, password, role) VALUES (?, ?, ?, 'Customer');";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                showAlert("Success", "New user created successfully.");
                stage.setScene(loginScene);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to create a new user.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setMinWidth(100);
    }
}
