import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class CustomerDashboard {

    private final int userId; // ID of the logged-in user
    private TableView<Book> bookTable;
    private TableView<Reservation> reservationTable; // Add this line

    public CustomerDashboard(int userId) {
        this.userId = userId;
    }

    /**
     * Displays the customer dashboard.
     */
    public void showDashboard() {
        VBox layout = new VBox(10);
        layout.getChildren().addAll(createBookTable(), createReservationTable());

        // Add reservation button
        Button reserveBookButton = new Button("Reserve Book");
        reserveBookButton.setOnAction(e -> reserveBook());

        // Add cancel reservation button
        Button cancelReservationButton = new Button("Cancel Reservation");
        cancelReservationButton.setOnAction(e -> cancelReservation());

        layout.getChildren().addAll(reserveBookButton, cancelReservationButton);

        // Display the layout (this can be set as a scene in your app's stage)
        System.out.println("Customer Dashboard Loaded...");
    }

    /**
     * Creates a table for displaying available books.
     *
     * @return TableView for books
     */
    private TableView<Book> createBookTable() {
        bookTable = new TableView<>();
        ObservableList<Book> books = FXCollections.observableArrayList();

        try {
            books.addAll(DBUtils.getAllBooks());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load books: " + e.getMessage());
        }

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

        TableColumn<Book, Integer> copiesAvailableColumn = new TableColumn<>("Available Copies");
        copiesAvailableColumn.setCellValueFactory(cell -> cell.getValue().copiesAvailableProperty().asObject());

        bookTable.setItems(books);
        bookTable.getColumns().addAll(titleColumn, authorColumn, publisherColumn, yearPublishedColumn, categoryColumn, copiesAvailableColumn);

        return bookTable;
    }

    /**
     * Creates a table for displaying the user's reservations.
     *
     * @return TableView for reservations
     */
    private TableView<Reservation> createReservationTable() {
        reservationTable = new TableView<>(); // Update this line
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();

        try {
            List<Reservation> reservationList = DBUtils.getReservationsByUserId(userId);
            if (reservationList != null) {
                reservations.addAll(reservationList);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No reservations found for the user.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reservations: " + e.getMessage());
        }

        TableColumn<Reservation, String> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(cell -> cell.getValue().bookIdProperty());

        TableColumn<Reservation, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());

        reservationTable.setItems(reservations);
        reservationTable.getColumns().addAll(bookIdColumn, statusColumn);

        return reservationTable;
    }

    /**
     * Handles book reservation.
     */
    private void reserveBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            int bookId = selectedBook.getBookId();
            try {
                DBUtils.reserveBook(userId, bookId);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book reserved successfully!");

                // Log the action
                LogUtils.logAction(userId, "Reserved book with ID: " + bookId);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reserve book: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "No book selected.");
        }
    }

    private void cancelReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem(); // Update this line
        if (selectedReservation != null) {
            int reservationId = selectedReservation.getReservationId();
            try {
                DBUtils.cancelReservation(reservationId);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation cancelled successfully!");
                reservationTable.getItems().remove(selectedReservation); // Remove the cancelled reservation from the table
                
                // Log the action
                LogUtils.logAction(userId, "Cancelled reservation with ID: " + reservationId);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel reservation: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "No reservation selected.");
        }
    }

    /**
     * Shows an alert message.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getLayout() {
        VBox layout = new VBox(10);
        layout.getChildren().addAll(createBookTable(), createReservationTable());

        // Add reservation button
        Button reserveBookButton = new Button("Reserve Book");
        reserveBookButton.setOnAction(e -> reserveBook());

        // Add cancel reservation button
        Button cancelReservationButton = new Button("Cancel Reservation");
        cancelReservationButton.setOnAction(e -> cancelReservation());

        layout.getChildren().addAll(reserveBookButton, cancelReservationButton);

        return layout;
    }
}
