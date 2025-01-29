package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO to represent an AgentContract.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AgentContractDTO {

    private Long id; // Contract ID
    private Double totalAmount; // Total contract amount
    private LocalDate createdAt; // Creation date of the contract
    private LocalDate startDate; // Contract start date
    private LocalDate endDate; // Contract end date
    private UUID medAgentId;
    private List<MedicineWithQuantityDTO> medicinesWithQuantities;

}