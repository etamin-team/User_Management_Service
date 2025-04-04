package com.example.user_management_service.model.dto;



import com.example.user_management_service.model.ContractMedicineAmount;
import com.example.user_management_service.model.Medicine;
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
    private Long quantityId;
    private Long medicineId;
    private Long quote;
    private Long correction;
    private Long agentContractId;
    private ContractMedicineAmount contractMedicineAmount;
    private Medicine medicine;
}
