package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerGoalWithDetailsDTO {

    private Long goalId;
    private UUID managerId;
    private List<Field> fields;

    // List of medicine IDs and names
    private List<Medicine> medicines;

    // List of district IDs and names
    private List<DistrictDTO> districts;

    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;

}

