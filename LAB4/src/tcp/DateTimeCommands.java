package tcp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateTimeCommands {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    private DateTimeCommands() {
    }

    public static String process(String request) {
        String command = request.trim().toUpperCase(Locale.ROOT);
        return switch (command) {
            case "DATE" -> LocalDate.now().format(DATE_FORMAT);
            case "TIME" -> LocalTime.now().format(TIME_FORMAT);
            case "DATETIME" -> LocalDateTime.now().format(DATE_TIME_FORMAT);
            case "QUIT" -> "OK BYE";
            default -> "ERR UNKNOWN_COMMAND";
        };
    }

    public static boolean isQuit(String request) {
        return "QUIT".equalsIgnoreCase(request.trim());
    }
}