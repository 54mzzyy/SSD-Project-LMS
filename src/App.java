import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("Library Management System");

        // Start with the Login screen
        showLoginScreen();
    }

    public void showLoginScreen() {
        Login login = new Login(stage, this);
        stage.setScene(new Scene(login.getLayout(), 800, 600));
        stage.show();
    }

    public void showSignUpScreen() {
        SignUp signUp = new SignUp(stage, this);
        stage.setScene(new Scene(signUp.getLayout(), 800, 600));
        stage.show();
    }

    public void showCustomerDashboard(String username) {
        CustomerDashboard customerDashboard = new CustomerDashboard(stage, this, username);
        stage.setScene(new Scene(customerDashboard.getLayout(), 800, 600));
        stage.show();
    }

    public void showLibrarianDashboard(String username) {
        LibrarianDashboard librarianDashboard = new LibrarianDashboard(stage, this, username);
        stage.setScene(new Scene(librarianDashboard.getLayout(), 800, 600));
        stage.show();
    }

    public void showAdministratorDashboard(String username) {
        AdministratorDashboard administratorDashboard = new AdministratorDashboard(stage, this, username);
        stage.setScene(new Scene(administratorDashboard.getLayout(), 800, 600));
        stage.show();
    }

    public void showChangePasswordScreen(String username) {
        ChangePassword changePassword = new ChangePassword(stage, this, username);
        stage.setScene(new Scene(changePassword.getLayout(), 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
