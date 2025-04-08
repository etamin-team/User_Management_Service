package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.ContractMedicineDoctorAmount;
import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Date-4/8/2025
 * By Sardor Tokhirov
 * Time-5:06 AM (GMT+5)
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineWithQuantityDoctorDTO {
    private Long quantityId;
    private Long medicineId;
    private Long quote;
    private Long correction;
    private Long agentContractId;
    private ContractMedicineDoctorAmount contractMedicineDoctorAmount;
    private Medicine medicine;
}