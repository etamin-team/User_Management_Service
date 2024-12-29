package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:43 PM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContractDTO {

    private Long id; // Contract ID (if updating an existing contract)
    private List<Long> medicineIds;  // List of medicine IDs for the contract
    private String contractDate; // Date of the contract
    private String contractType; // Type of the contract (e.g., short-term, long-term)
    private String contractStatus; // Status of the contract (e.g., active, expired)
    private Double totalAmount; // Total amount for the contract

}