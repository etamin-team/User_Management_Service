package com.example.user_management_service.model.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO to represent a Medicine with its quantity in the contract.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineWithQuantityDTO {

    private Long medicineId; // ID of the medicine
    private String medicineName; // Name of the medicine
    private Integer quote; // Quantity to sell

}
