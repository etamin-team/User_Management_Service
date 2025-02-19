package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.PreparationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PreparationDto {

    private String name;
    private String amount;
    private Integer quantity;
    private Integer timesInDay;
    private Integer days;
    private PreparationType type;
    private Long medicineId;
    private Medicine medicine;
}