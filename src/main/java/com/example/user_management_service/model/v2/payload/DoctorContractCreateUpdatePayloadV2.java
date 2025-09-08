package com.example.user_management_service.model.v2.payload;

import com.example.user_management_service.model.ContractType;
import com.example.user_management_service.model.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Date-9/7/2025
 * By Sardor Tokhirov
 * Time-9:24 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorContractCreateUpdatePayloadV2 {
    private Long contractId;

    private GoalStatus goalStatus;

    private UUID creatorId;

    List<MedicineQuotePayloadV2> medicineQuotes;

    private ContractType contractType;

    private LocalDate startDate;

    private LocalDate endDate;
}
