package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-2/21/2025
 * By Sardor Tokhirov
 * Time-12:58 PM (GMT+5)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesRegionDTO {
    private Long id;
    private Long regionId;
    private Long allDirectSales;
    private Long allSecondarySales;
    private Long quote;
    private Long total;

}
