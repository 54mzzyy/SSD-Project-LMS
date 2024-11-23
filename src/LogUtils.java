import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtils {

    private static final String LOG_FILE_PATH = ".\\application_logs.txt"; // Log file path

    /**
     * Logs an action to the log file.
     *
     * @param userId ID of the user performing the action
     * @param action Description of the action performed
     */
    public static synchronized void logAction(int userId, String action) {
        String logEntry = String.format("[%s] User ID: %d - Action: %s%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                userId,
                action);

        // Ensure the log file exists
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            try {
                boolean fileCreated = logFile.createNewFile();
                if (fileCreated) {
                    System.out.println("Log file created: " + logFile.getAbsolutePath());
                } else {
                    System.out.println("Log file already exists.");
                }
            } catch (IOException e) {
                System.err.println("Failed to create log file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Write to the log file
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) { // Append mode
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
