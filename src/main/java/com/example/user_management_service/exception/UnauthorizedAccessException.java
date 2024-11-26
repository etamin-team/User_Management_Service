package com.example.user_management_service.exception;

/**
 * Custom exception thrown when a user attempts to perform an unauthorized action.
 */
public class UnauthorizedAccessException extends RuntimeException {

    // Default constructor with a generic error message
    public UnauthorizedAccessException() {
        super("You are not authorized to perform this action.");
    }

    // Constructor with a custom error message
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    // Constructor with a custom error message and a cause
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with only a cause
    public UnauthorizedAccessException(Throwable cause) {
        super(cause);
    }
}
