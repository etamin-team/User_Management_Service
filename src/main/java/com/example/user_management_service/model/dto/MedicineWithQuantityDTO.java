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

    private Long medicineId;
    private String medicineName;
    private Integer quote;

}
