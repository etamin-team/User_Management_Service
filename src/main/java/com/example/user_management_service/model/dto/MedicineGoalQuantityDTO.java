package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.ContractMedicineAmount;
import com.example.user_management_service.model.Medicine;
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
    private Long quote;
    private Long medicineId;
    private Long managerGoalId;
    private ContractMedicineAmount contractMedicineAmount;
    private Medicine medicine;
}