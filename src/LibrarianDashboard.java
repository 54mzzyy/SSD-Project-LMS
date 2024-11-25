import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class LibrarianDashboard {
    private VBox layout;
    private TableView<Reservation> reservationTable;
    private TableView<Transaction> transactionTable;
    private TableView<Book> bookTable;

    public LibrarianDashboard(Stage stage, App app, String username) {
        layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Librarian Dashboard");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label welcomeLabel = new Label("Welcome, " + username);

        Button manageBooksButton = new Button("Manage Books");
        manageBooksButton.setOnAction(e -> showManageBooksScreen());

        Button viewReservationsButton = new Button("View Reservations");
        viewReservationsButton.setOnAction(e -> showReservationsScreen());

        Button viewTransactionsButton = new Button("View Transactions");
        viewTransactionsButton.setOnAction(e -> showTransactionsScreen());

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> app.showChangePasswordScreen(username));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> app.showLoginScreen());

        layout.getChildren().addAll(titleLabel, welcomeLabel, manageBooksButton, viewReservationsButton, viewTransactionsButton, changePasswordButton, logoutButton);
    }

    public VBox getLayout() {
        return layout;
    }

    private void showManageBooksScreen() {
        Stage bookStage = new Stage();
        bookStage.setTitle("Manage Books");

        bookTable = new TableView<>();
        bookTable.setItems(getAllBooks());

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());

        TableColumn<Book, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(cellData -> cellData.getValue().publisherProperty());

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));

        TableColumn<Book, Number> yearColumn = new TableColumn<>("Year Published");
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearPublishedProperty());

        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Book, Number> copiesColumn = new TableColumn<>("Copies Available");
        copiesColumn.setCellValueFactory(cellData -> cellData.getValue().copiesAvailableProperty());

        bookTable.getColumns().addAll(titleColumn, authorColumn, publisherColumn, isbnColumn, yearColumn, categoryColumn, copiesColumn);

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> showAddBookDialog());

        Button editBookButton = new Button("Edit Book");
        editBookButton.setOnAction(e -> showEditBookDialog());

        Button deleteBookButton = new Button("Delete Book");
        deleteBookButton.setOnAction(e -> deleteSelectedBook());

        HBox buttons = new HBox(10, addBookButton, editBookButton, deleteBookButton);
        buttons.setAlignment(Pos.CENTER);

        VBox bookLayout = new VBox(10, bookTable, buttons);
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

        TableColumn<Reservation, Number> reservationIdColumn = new TableColumn<>("Reservation ID");
        reservationIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getReservationId()));

        TableColumn<Reservation, Number> userIdColumn = new TableColumn<>("User ID");
        userIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()));

        TableColumn<Reservation, Number> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBookId()));

        TableColumn<Reservation, String> reservationDateColumn = new TableColumn<>("Reservation Date");
        reservationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));

        TableColumn<Reservation, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        reservationTable.getColumns().addAll(reservationIdColumn, userIdColumn, bookIdColumn, reservationDateColumn, statusColumn);

        Button completeReservationButton = new Button("Complete Reservation");
        completeReservationButton.setOnAction(e -> completeSelectedReservation());

        Button cancelReservationButton = new Button("Cancel Reservation");
        cancelReservationButton.setOnAction(e -> cancelSelectedReservation());

        HBox buttons = new HBox(10, completeReservationButton, cancelReservationButton);
        buttons.setAlignment(Pos.CENTER);

        VBox reservationLayout = new VBox(10, reservationTable, buttons);
        reservationLayout.setPadding(new Insets(10));

        Scene scene = new Scene(reservationLayout, 800, 600);
        reservationStage.setScene(scene);
        reservationStage.show();
    }

    private void showTransactionsScreen() {
        Stage transactionStage = new Stage();
        transactionStage.setTitle("View Transactions");

        transactionTable = new TableView<>();
        transactionTable.setItems(getAllTransactions());

        TableColumn<Transaction, Number> transactionIdColumn = new TableColumn<>("Transaction ID");
        transactionIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTransactionId()));

        TableColumn<Transaction, Number> userIdColumn = new TableColumn<>("User ID");
        userIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()));

        TableColumn<Transaction, Number> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBookId()));

        TableColumn<Transaction, String> transactionTypeColumn = new TableColumn<>("Transaction Type");
        transactionTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactionType()));

        TableColumn<Transaction, String> transactionDateColumn = new TableColumn<>("Transaction Date");
        transactionDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactionDate()));

        TableColumn<Transaction, String> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDueDate()));

        TableColumn<Transaction, String> returnDateColumn = new TableColumn<>("Return Date");
        returnDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReturnDate()));

        TableColumn<Transaction, Number> fineColumn = new TableColumn<>("Fine");
        fineColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getFine()));

        transactionTable.getColumns().addAll(transactionIdColumn, userIdColumn, bookIdColumn, transactionTypeColumn, transactionDateColumn, dueDateColumn, returnDateColumn, fineColumn);

        VBox transactionLayout = new VBox(10, transactionTable);
        transactionLayout.setPadding(new Insets(10));

        Scene scene = new Scene(transactionLayout, 800, 600);
        transactionStage.setScene(scene);
        transactionStage.show();
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
            List<Reservation> reservations = DBUtils.getAllReservations();
            return FXCollections.observableArrayList(reservations);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    private ObservableList<Transaction> getAllTransactions() {
        try {
            List<Transaction> transactions = DBUtils.getAllTransactions();
            return FXCollections.observableArrayList(transactions);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    private void showAddBookDialog() {
        // Implement the dialog to add a new book
    }

    private void showEditBookDialog() {
        // Implement the dialog to edit the selected book
    }

    private void deleteSelectedBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            // Implement the logic to delete the selected book
        }
    }

    private void completeSelectedReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                DBUtils.completeReservation(selectedReservation.getReservationId());
                reservationTable.setItems(getAllReservations());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void cancelSelectedReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                DBUtils.cancelReservation(selectedReservation.getReservationId());
                reservationTable.setItems(getAllReservations());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
