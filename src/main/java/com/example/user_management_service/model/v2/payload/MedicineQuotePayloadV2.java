package com.example.user_management_service.model.v2.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:01 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineQuotePayloadV2 {
    private Long medicineId;
    private Long quote;
}
