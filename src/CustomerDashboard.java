import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class CustomerDashboard {

    private final int userId; // ID of the logged-in user
    private TableView<Book> bookTable;
    private TableView<Reservation> reservationTable;
    private TextField searchField;
    private ObservableList<Book> books;
    private ObservableList<Reservation> reservations;
    private App app;
    private String username;

    public CustomerDashboard(int userId, App app, String username) {
        this.userId = userId;
        this.app = app;
        this.username = username;
    }

    public void showDashboard(Stage stage) {
        VBox layout = getLayout();

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public VBox getLayout() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Customer Dashboard");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Button viewBooksButton = new Button("View Books");
        viewBooksButton.setOnAction(e -> showBooksScreen());

        Button viewReservationsButton = new Button("View Reservations");
        viewReservationsButton.setOnAction(e -> showReservationsScreen());

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> app.showChangePasswordScreen(username));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            LogUtils.logAction(userId, "User logged out");
            app.showLoginScreen();
        });

        layout.getChildren().addAll(titleLabel, viewBooksButton, viewReservationsButton, changePasswordButton,
                logoutButton);

        return layout;
    }

    private void showBooksScreen() {
        Stage bookStage = new Stage();
        bookStage.setTitle("View Books");

        bookTable = new TableView<>();
        FilteredList<Book> filteredData = new FilteredList<>(getAllBooks(), p -> true);

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (book.getPublisher().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(book.getYearPublished()).contains(lowerCaseFilter)) {
                    return true;
                } else if (book.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bookTable.comparatorProperty());

        bookTable.setItems(sortedData);

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cell -> cell.getValue().authorProperty());

        TableColumn<Book, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(cell -> cell.getValue().publisherProperty());

        TableColumn<Book, Integer> yearPublishedColumn = new TableColumn<>("Year Published");
        yearPublishedColumn.setCellValueFactory(cell -> cell.getValue().yearPublishedProperty().asObject());

        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cell -> cell.getValue().categoryProperty());

        bookTable.getColumns().addAll(titleColumn, authorColumn, publisherColumn, yearPublishedColumn, categoryColumn);

        Button reserveBookButton = new Button("Reserve Book");
        reserveBookButton.setOnAction(e -> reserveBook());

        VBox bookLayout = new VBox(10, searchField, bookTable, reserveBookButton);
        bookLayout.setPadding(new Insets(10));

        Scene scene = new Scene(bookLayout, 800, 600);
        bookStage.setScene(scene);
        bookStage.show();
    }

    private void showReservationsScreen() {
        Stage reservationStage = new Stage();
        reservationStage.setTitle("View Reservations");

        reservationTable = new TableView<>();
        reservationTable.setItems(getAllReservations());

        TableColumn<Reservation, String> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(cell -> cell.getValue().bookIdProperty());

        TableColumn<Reservation, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());

        TableColumn<Reservation, String> bookTitleColumn = new TableColumn<>("Book Title");
        bookTitleColumn.setCellValueFactory(cell -> cell.getValue().bookTitleProperty());

        TableColumn<Reservation, String> bookAuthorColumn = new TableColumn<>("Author");
        bookAuthorColumn.setCellValueFactory(cell -> cell.getValue().bookAuthorProperty());

        TableColumn<Reservation, String> bookCategoryColumn = new TableColumn<>("Category");
        bookCategoryColumn.setCellValueFactory(cell -> cell.getValue().bookCategoryProperty());

        reservationTable.getColumns().addAll(bookIdColumn, statusColumn, bookTitleColumn, bookAuthorColumn,
                bookCategoryColumn);

        Button cancelReservationButton = new Button("Cancel Reservation");
        cancelReservationButton.setOnAction(e -> cancelReservation());

        VBox reservationLayout = new VBox(10, reservationTable, cancelReservationButton);
        reservationLayout.setPadding(new Insets(10));

        Scene scene = new Scene(reservationLayout, 800, 600);
        reservationStage.setScene(scene);
        reservationStage.show();
    }

    private ObservableList<Book> getAllBooks() {
        try {
            List<Book> books = DBUtils.getAllBooks();
            return FXCollections.observableArrayList(books);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    private ObservableList<Reservation> getAllReservations() {
        try {
            List<Reservation> reservations = DBUtils.getReservationsByUserId(userId);
            return FXCollections.observableArrayList(reservations);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    private void reserveBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            int bookId = selectedBook.getBookId();
            try {
                DBUtils.reserveBook(userId, bookId);
                DBUtils.borrowBook(userId, bookId);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book reserved and borrowed successfully!");
                bookTable.setItems(getAllBooks());
                reservationTable.setItems(getAllReservations());
                LogUtils.logAction(userId, "Reserved and borrowed book with ID: " + bookId);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reserve book: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "No book selected.");
        }
    }

    private void cancelReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            int reservationId = selectedReservation.getReservationId();
            int bookId = selectedReservation.getBookId();
            try {
                DBUtils.cancelReservation(reservationId);
                DBUtils.returnBook(userId, bookId);
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Reservation cancelled and book returned successfully!");
                bookTable.setItems(getAllBooks());
                reservationTable.setItems(getAllReservations());
                LogUtils.logAction(userId, "Cancelled reservation and returned book with ID: " + reservationId);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel reservation: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "No reservation selected.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
