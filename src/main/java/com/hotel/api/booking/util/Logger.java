package com.hotel.api.booking.util;

import com.hotel.api.booking.exception.ApplicationException;

import java.util.Arrays;
import java.util.logging.Level;


public class Logger {
    private final java.util.logging.Logger logger;

    public Logger(Object parent) {
        logger = java.util.logging.Logger.getLogger(parent.getClass().getName());
    }

    public Logger(String parent) {
        logger = java.util.logging.Logger.getLogger(parent);
    }

    public void logException(Exception exception) {
        logException(Level.SEVERE, exception);
    }

    public void logException(Exception exception, String message) {
        logException(Level.SEVERE, message + "(" + exception.getMessage() + ")", exception);
    }

    public void logException(Level level, Exception exception) {
        logException(level, exception.getMessage(), exception);
    }

    public void logException(Level level, String message, Exception exception) {
        String errorMessage = "Something went wrong: Exception = " +
                exception.getClass().getName() +
                "\nStackTrace: \n\t" +
                Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .reduce(((s, s2) -> s + "\n\t" + s2)) +
                "\nDescription: " + message;
        if (exception instanceof ApplicationException applicationException) {
            errorMessage += "\nLocation: " + applicationException.getCode();
        }
        logger.log(level, errorMessage);
    }

    public void log(String message) {
        logger.log(Level.INFO, message);
    }

    public void log(Level level, String message) {
        logger.log(level, message);
    }
}
