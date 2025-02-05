package com.example.user_management_service.exception;

/**
 * Date-2/5/2025
 * By Sardor Tokhirov
 * Time-7:24 PM (GMT+5)
 */
public class DoctorContractExistsException extends RuntimeException{
    public DoctorContractExistsException(String message) {
        super(message);
    }
}
