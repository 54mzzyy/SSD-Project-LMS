import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.Optional;

public class LibrarianDashboard {
    private final int userId;
    private VBox layout;
    private TableView<Reservation> reservationTable;
    private TableView<Transaction> transactionTable;
    private TableView<Book> bookTable;

    public LibrarianDashboard(Stage stage, App app, String username, int userId) {
        this.userId = userId;
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
        logoutButton.setOnAction(e -> {
            LogUtils.logAction(userId, "User logged out");
            app.showLoginScreen();
        });

        layout.getChildren().addAll(titleLabel, welcomeLabel, manageBooksButton, viewReservationsButton, viewTransactionsButton, changePasswordButton, logoutButton);
    }

    public VBox getLayout() {
        return layout;
    }

    private void showManageBooksScreen() {
        Stage bookStage = new Stage();
        bookStage.setTitle("Manage Books");

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

        VBox bookLayout = new VBox(10, searchField, bookTable, buttons);
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
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the fields for the book details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        TextField publisherField = new TextField();
        publisherField.setPromptText("Publisher");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField yearField = new TextField();
        yearField.setPromptText("Year Published");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextField copiesField = new TextField();
        copiesField.setPromptText("Copies Available");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Publisher:"), 0, 2);
        grid.add(publisherField, 1, 2);
        grid.add(new Label("ISBN:"), 0, 3);
        grid.add(isbnField, 1, 3);
        grid.add(new Label("Year Published:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Category:"), 0, 5);
        grid.add(categoryField, 1, 5);
        grid.add(new Label("Copies Available:"), 0, 6);
        grid.add(copiesField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Book object when the Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Book(
                    0, // Book ID will be auto-generated
                    titleField.getText(),
                    authorField.getText(),
                    publisherField.getText(),
                    isbnField.getText(),
                    Integer.parseInt(yearField.getText()),
                    categoryField.getText(),
                    Integer.parseInt(copiesField.getText())
                );
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            try {
                DBUtils.addBook(book);
                // Refresh the book table
                bookTable.getItems().add(book);
                // Log the action
                LogUtils.logAction(userId, "Added new book: " + book.getTitle());
            } catch (SQLException e) {
                e.printStackTrace();
                // Log the error
                LogUtils.logAction(userId, "Failed to add new book: " + book.getTitle() + " - " + e.getMessage());
            }
        });
    }

    private void showEditBookDialog() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            // Show an alert if no book is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Book Selected");
            alert.setContentText("Please select a book to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");

        // Set the button types
        ButtonType editButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);

        // Create the fields for the book details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(selectedBook.getTitle());
        TextField authorField = new TextField(selectedBook.getAuthor());
        TextField publisherField = new TextField(selectedBook.getPublisher());
        TextField isbnField = new TextField(selectedBook.getIsbn());
        TextField yearField = new TextField(String.valueOf(selectedBook.getYearPublished()));
        TextField categoryField = new TextField(selectedBook.getCategory());
        TextField copiesField = new TextField(String.valueOf(selectedBook.getCopiesAvailable()));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Publisher:"), 0, 2);
        grid.add(publisherField, 1, 2);
        grid.add(new Label("ISBN:"), 0, 3);
        grid.add(isbnField, 1, 3);
        grid.add(new Label("Year Published:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Category:"), 0, 5);
        grid.add(categoryField, 1, 5);
        grid.add(new Label("Copies Available:"), 0, 6);
        grid.add(copiesField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Book object when the Save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                selectedBook.setTitle(titleField.getText());
                selectedBook.setAuthor(authorField.getText());
                selectedBook.setPublisher(publisherField.getText());
                selectedBook.setIsbn(isbnField.getText());
                selectedBook.setYearPublished(Integer.parseInt(yearField.getText()));
                selectedBook.setCategory(categoryField.getText());
                selectedBook.setCopiesAvailable(Integer.parseInt(copiesField.getText()));
                return selectedBook;
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            try {
                DBUtils.updateBook(book);
                // Refresh the book table
                bookTable.refresh();
                // Log the action
                LogUtils.logAction(userId, "Edited book: " + book.getTitle());
            } catch (SQLException e) {
                e.printStackTrace();
                // Log the error
                LogUtils.logAction(userId, "Failed to edit book: " + book.getTitle() + " - " + e.getMessage());
            }
        });
    }

    private void deleteSelectedBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Book");
            alert.setHeaderText("Are you sure you want to delete this book?");
            alert.setContentText("Title: " + selectedBook.getTitle());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    DBUtils.deleteBook(selectedBook.getBookId());
                    // Remove the book from the table
                    bookTable.getItems().remove(selectedBook);
                    // Log the action
                    LogUtils.logAction(userId, "Deleted book: " + selectedBook.getTitle());
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Log the error
                    LogUtils.logAction(userId, "Failed to delete book: " + selectedBook.getTitle() + " - " + e.getMessage());
                }
            }
        } else {
            // Show an alert if no book is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Book Selected");
            alert.setContentText("Please select a book to delete.");
            alert.showAndWait();
        }
    }

    private void completeSelectedReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                DBUtils.completeReservation(selectedReservation.getReservationId());
                reservationTable.setItems(getAllReservations());
                // Log the action
                LogUtils.logAction(userId, "Completed reservation: " + selectedReservation.getReservationId());
            } catch (SQLException e) {
                e.printStackTrace();
                // Log the error
                LogUtils.logAction(userId, "Failed to complete reservation: " + selectedReservation.getReservationId() + " - " + e.getMessage());
            }
        }
    }

    private void cancelSelectedReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            try {
                DBUtils.cancelReservation(selectedReservation.getReservationId());
                reservationTable.setItems(getAllReservations());
                // Log the action
                LogUtils.logAction(userId, "Cancelled reservation: " + selectedReservation.getReservationId());
            } catch (SQLException e) {
                e.printStackTrace();
                // Log the error
                LogUtils.logAction(userId, "Failed to cancel reservation: " + selectedReservation.getReservationId() + " - " + e.getMessage());
            }
        }
    }
}
