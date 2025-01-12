package org.group05.com.logging.strategy.impl;

import org.group05.com.logging.strategy.LoggingStrategy;
import org.group05.com.utils.LoggingUtils;


public class ConsoleLogging implements LoggingStrategy {
    @Override
    public void log(String message) {
        System.out.println(LoggingUtils.getLoggingMessage(message, "INFO"));
    }
}
