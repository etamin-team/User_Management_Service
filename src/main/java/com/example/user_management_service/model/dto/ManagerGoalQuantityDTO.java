package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManagerGoalQuantityDTO {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private Integer quote;
    private Long managerGoalId;
}