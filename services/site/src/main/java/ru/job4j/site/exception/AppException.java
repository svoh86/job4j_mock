package ru.job4j.site.exception;

/**
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}
