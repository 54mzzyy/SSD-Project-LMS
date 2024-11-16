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
import javafx.stage.Stage;

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
        loginLayout.setPadding(new Insets(10));

        Button loginButton = new Button("Sign In");
        loginButton.setOnAction(event -> authenticate());

        Label orLabel = new Label("or");
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> showSignUpScene());

        loginLayout.getChildren().addAll(new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton, orLabel, signUpButton);

        loginScene = new Scene(loginLayout, 300, 250);
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
                String role = rs.getString("role"); // Get the role from the result set

                // Redirect to the appropriate home page
                if (role.equals("Customer")) {
                    CustomerHome customerHome = new CustomerHome(stage, username);
                    customerHome.initializeComponents();
                } else if (role.equals("Librarian")) {
                    LibrarianHome librarianHome = new LibrarianHome(stage, username);
                    librarianHome.initializeComponents();
                } else if (role.equals("Administrator")) {
                    AdministratorHome adminHome = new AdministratorHome(stage, username);
                    adminHome.initializeComponents();
                } else {
                    showAlert("Error", "Unknown role.");
                }
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
        signUpLayout.setPadding(new Insets(10));

        TextField newUsername = new TextField();
        PasswordField newPassword = new PasswordField();
        TextField emailField = new TextField();

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(e -> {
            createNewUser(newUsername.getText(), newPassword.getText(), emailField.getText());
        });

        signUpLayout.getChildren().addAll(new Label("Username:"), newUsername,
                new Label("Password:"), newPassword,
                new Label("Email:"), emailField,
                createAccountButton);

        Scene signUpScene = new Scene(signUpLayout, 300, 300);
        stage.setScene(signUpScene);
    }

    private void createNewUser(String username, String password, String email) {
        String query = "INSERT INTO users (user_name, password, role, email) VALUES (?, ?, 'Customer', ?);";

        try (Connection con = DBUtils.establishConnection();
                PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                showAlert("Success", "New user created successfully.");
                initializeComponents(); // Go back to login page after sign-up
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
}
