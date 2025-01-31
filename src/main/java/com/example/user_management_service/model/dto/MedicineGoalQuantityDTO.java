package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.ContractMedicineAmount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineGoalQuantityDTO {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private Long quote;
    private Long managerGoalId;
    private ContractMedicineAmount contractMedicineAmount;
}