package org.group05.com.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getLoggingMessage(String message, String level) {
        String timestamp = LocalDateTime.now().format(formatter);
       return "[" + timestamp + "] [" + level.toUpperCase() + "] - " + message;
    }
}
