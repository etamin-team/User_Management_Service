package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-5/29/2025
 * By Sardor Tokhirov
 * Time-11:53 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesQuoteDTO {
    private Long quote;
    private Long sales;
}
