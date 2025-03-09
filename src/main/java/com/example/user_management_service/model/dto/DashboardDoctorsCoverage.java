package com.example.user_management_service.model.dto;

import java.time.Month;

/**
 * Date-3/9/2025
 * By Sardor Tokhirov
 * Time-7:20 AM (GMT+5)
 */
public class DashboardDoctorsCoverage {
    public Long doctorsNumber;
    public Long doctorsWithContract;
    public Month month;

    public DashboardDoctorsCoverage(Long doctorsNumber, Long doctorsWithContract, Integer month) {
        this.doctorsNumber = (doctorsNumber != null) ? doctorsNumber : 0L;
        this.doctorsWithContract = (doctorsWithContract != null) ? doctorsWithContract : 0L;
        this.month = (month != null) ? Month.of(month) : null;
    }

    // Getters
    public Long getDoctorsNumber() { return doctorsNumber; }
    public Long getDoctorsWithContract() { return doctorsWithContract; }
    public Month getMonth() { return month; }
}