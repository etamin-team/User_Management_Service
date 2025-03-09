package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.ContractType;

/**
 * Date-3/9/2025
 * By Sardor Tokhirov
 * Time-8:40 AM (GMT+5)
 */
public class ContractTypeSalesData {
    private ContractType contractType;
    private Long amount;

    public ContractTypeSalesData(ContractType contractType, Long amount) {
        this.contractType = contractType;
        this.amount = (amount != null) ? amount : 0L;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public Long getAmount() {
        return amount;
    }
}
