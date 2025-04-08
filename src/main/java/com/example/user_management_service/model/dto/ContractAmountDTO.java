package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.ContractType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContractAmountDTO {

    private Long id;
    private UUID doctorId;

    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long fieldId;
    private UUID agentId;
    private ContractType contractType;

    private List<MedicineWithQuantityDoctorDTO> medicineWithQuantityDoctorDTOS;
}