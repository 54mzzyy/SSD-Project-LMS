import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Reservation {
    private int reservationId;
    private int userId;
    private int bookId;
    private String reservationDate;
    private StringProperty status; // Pending, Completed, or Cancelled
    private StringProperty bookTitle;
    private StringProperty bookAuthor;
    private StringProperty bookCategory;

    // Constructor
    public Reservation(int reservationId, int userId, int bookId, String reservationDate, String status, String bookTitle, String bookAuthor, String bookCategory) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.bookId = bookId;
        this.reservationDate = reservationDate;
        this.status = new SimpleStringProperty(status);
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.bookAuthor = new SimpleStringProperty(bookAuthor);
        this.bookCategory = new SimpleStringProperty(bookCategory);
    }

    // Getters and Setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty bookIdProperty() {
        return new SimpleStringProperty(String.valueOf(bookId));
    }

    public String getBookTitle() {
        return bookTitle.get();
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle.set(bookTitle);
    }

    public StringProperty bookTitleProperty() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor.get();
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor.set(bookAuthor);
    }

    public StringProperty bookAuthorProperty() {
        return bookAuthor;
    }

    public String getBookCategory() {
        return bookCategory.get();
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory.set(bookCategory);
    }

    public StringProperty bookCategoryProperty() {
        return bookCategory;
    }
}
