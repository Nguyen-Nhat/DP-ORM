package org.group05.com.logging.strategy.impl;

import org.group05.com.logging.strategy.LoggingStrategy;
import org.group05.com.utils.LoggingUtils;

import java.io.FileWriter;
import java.io.IOException;

public class FileLogging implements LoggingStrategy {
    private String fileName;

    public FileLogging(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void log(String message) {
        String loggingMessage = LoggingUtils.getLoggingMessage(message, "INFO");
        try (FileWriter writer = new FileWriter(fileName, true)) { // Mở file ở chế độ append
            writer.write(loggingMessage + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
