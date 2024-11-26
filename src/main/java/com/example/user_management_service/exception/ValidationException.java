package com.example.user_management_service.exception;


import com.example.user_management_service.auth.ErrorMessage;

public class ValidationException extends RuntimeException {
    private final ErrorMessage errorMessage;

    // Constructor to pass ErrorMessage
    public ValidationException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());  // Pass the error message to the parent exception class
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
