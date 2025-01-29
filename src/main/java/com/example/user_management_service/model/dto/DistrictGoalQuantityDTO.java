package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DistrictGoalQuantityDTO {
    private Long id;
    private Long districtId;
    private String districtName;
    private Integer quote;
    private Long managerGoalId;
}

