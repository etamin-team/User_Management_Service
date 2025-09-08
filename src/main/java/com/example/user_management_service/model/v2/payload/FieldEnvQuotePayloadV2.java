package com.example.user_management_service.model.v2.payload;

import com.example.user_management_service.model.Field;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:04 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldEnvQuotePayloadV2 {

    private Field field;

    @Column(name = "field_quote")
    private Long fieldQuote;
}
