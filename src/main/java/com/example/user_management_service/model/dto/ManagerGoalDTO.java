package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerGoalDTO {

    private Long goalId;
    private UUID managerId;
    private List<FieldGoalQuantityDTO> fieldGoalQuantities;
    private List<MedicineGoalQuantityDTO> medicineGoalQuantities;
    private List<DistrictGoalQuantityDTO> districtGoalQuantities;
    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;
}
