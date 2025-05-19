package com.example.user_management_service.exception;

import com.example.user_management_service.model.MNN;

import java.util.List;

/**
 * Date-5/19/2025
 * By Sardor Tokhirov
 * Time-3:14 PM (GMT+5)
 */
public class BulkSaveException extends RuntimeException {
    private final List<ErrorDetail> errors;

    public BulkSaveException(List<ErrorDetail> errors) {
        super("Failed to save some MNNs");
        this.errors = errors;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public record ErrorDetail(MNN mnn, String errorMessage) {
    }
}