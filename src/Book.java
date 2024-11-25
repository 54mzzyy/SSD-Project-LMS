import javafx.beans.property.*;

public class Book {
    private int bookId;
    private StringProperty title;
    private StringProperty author;
    private StringProperty publisher;
    private String isbn;
    private IntegerProperty yearPublished;
    private StringProperty category;
    private IntegerProperty copiesAvailable;

    // Constructor
    public Book(int bookId, String title, String author, String publisher, String isbn, int yearPublished, String category, int copiesAvailable) {
        this.bookId = bookId;
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.publisher = new SimpleStringProperty(publisher);
        this.isbn = isbn;
        this.yearPublished = new SimpleIntegerProperty(yearPublished);
        this.category = new SimpleStringProperty(category);
        this.copiesAvailable = new SimpleIntegerProperty(copiesAvailable);
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getPublisher() {
        return publisher.get();
    }

    public void setPublisher(String publisher) {
        this.publisher.set(publisher);
    }

    public StringProperty publisherProperty() {
        return publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getYearPublished() {
        return yearPublished.get();
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished.set(yearPublished);
    }

    public IntegerProperty yearPublishedProperty() {
        return yearPublished;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public int getCopiesAvailable() {
        return copiesAvailable.get();
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable.set(copiesAvailable);
    }

    public IntegerProperty copiesAvailableProperty() {
        return copiesAvailable;
    }
}
