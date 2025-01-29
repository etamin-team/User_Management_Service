package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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

    private Long id;
    private String doctorId;
    private List<Long> medicineIds;

    private Long  totalAmount;

    private LocalDate createdAt;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<MedicineWithQuantityDTO> medicinesWithQuantities;
}
