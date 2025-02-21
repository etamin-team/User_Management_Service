package com.example.user_management_service.model.dto;
import lombok.*;

/**
 * Date-2/21/2025
 * By Sardor Tokhirov
 * Time-2:30 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PercentageValDto {
    private Integer minPercentage;
    private Integer minPercentageVal;
    private Integer maxPercentage;
    private Integer maxPercentageVal;
}