package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Field;
import lombok.*;

/**
 * Date-2/28/2025
 * By Sardor Tokhirov
 * Time-5:57 PM (GMT+5)
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegionFieldDTO {
    private Field field;
    private Long amount;
}
