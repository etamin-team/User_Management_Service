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
    private String contractType; // Type of contract
    private String contractStatus; // Status of contract
    private Double totalAmount; // Total contract amount
    private Double quota60; // Quota for 60%
    private Double quota75To90; // Quota for 75-90%
    private Double su; // SU value
    private Double sb; // SB value
    private Double gz; // GZ value
    private Double kb; // KB value
    private LocalDate createdAt; // Creation date of the contract
    private LocalDate startDate; // Contract start date
    private LocalDate endDate; // Contract end date
    private UUID medAgentId;
    private List<MedicineWithQuantityDTO> medicinesWithQuantities;

}