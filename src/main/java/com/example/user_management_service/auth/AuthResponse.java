package com.example.user_management_service.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-11/21/2024
 * By Sardor Tokhirov
 * Time-1:50 PM (GMT+5)
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    @JsonProperty("access_token")
    private String accsesToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
}