package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OutOfContractMedicineAmountDTO {

    private Long id;
    private Long amount;
    private Medicine medicine;
}