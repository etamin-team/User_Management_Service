package com.example.user_management_service.exception;

/**
 * Date-2/5/2025
 * By Sardor Tokhirov
 * Time-7:14 PM (GMT+5)
 */
public class AgentContractExistsException extends RuntimeException {
    public AgentContractExistsException(String message) {
        super(message);
    }
}
