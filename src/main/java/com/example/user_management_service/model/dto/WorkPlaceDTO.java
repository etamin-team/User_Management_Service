package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-11:37 AM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkPlaceDTO {

    private Long id;
    private String name;
    private String address;
    private String description;
    private Long cityId;
}
