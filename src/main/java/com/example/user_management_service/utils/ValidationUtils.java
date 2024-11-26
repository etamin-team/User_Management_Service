package com.example.user_management_service.utils;

import com.example.user_management_service.model.dto.DoctorSignUpRequest;

/**
 * Date-11/25/2024
 * By Sardor Tokhirov
 * Time-3:48 AM (GMT+5)
 */
public class ValidationUtils {
    public static boolean hasNullFields(DoctorSignUpRequest request) {
        return request.getFirstName() == null ||
                request.getPhoneNumber() == null ||
                request.getPhonePrefix() == null ||
                request.getLastName() == null ||
                request.getPassword() == null ||
                request.getRegion() == null ||
                request.getCountry() == null;
    }
}
