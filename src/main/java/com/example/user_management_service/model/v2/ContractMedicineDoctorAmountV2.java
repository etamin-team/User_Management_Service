package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.MedicineWithQuantityDoctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-2:50 AM (GMT+5)
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Entity
@Table(name = "contract_medicine_doctor_amounts_v2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractMedicineDoctorAmountV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "correction")
    private Long correction;

    @Column(name = "year_month")
    private YearMonth yearMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_with_quantity_doctor_v2_id")
    private MedicineWithQuantityDoctorV2 medicineWithQuantityDoctor;
}