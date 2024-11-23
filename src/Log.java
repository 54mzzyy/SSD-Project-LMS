import javafx.beans.property.*;

public class Log {
    private final IntegerProperty logId;
    private final StringProperty action;
    private final StringProperty timestamp;

    public Log(int logId, String action, String timestamp) {
        this.logId = new SimpleIntegerProperty(logId);
        this.action = new SimpleStringProperty(action);
        this.timestamp = new SimpleStringProperty(timestamp);
    }

    public IntegerProperty logIdProperty() {
        return logId;
    }

    public StringProperty actionProperty() {
        return action;
    }

    public StringProperty timestampProperty() {
        return timestamp;
    }

    public int getLogId() {
        return logId.get();
    }

    public String getAction() {
        return action.get();
    }

    public String getTimestamp() {
        return timestamp.get();
    }
}
