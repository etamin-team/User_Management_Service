package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Date-3/2/2025
 * By Sardor Tokhirov
 * Time-7:57 AM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopProductsOnSellDTO {
    private Medicine medicine;
    private Long amount;
}
