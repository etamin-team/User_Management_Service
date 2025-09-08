package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Date-9/8/2025
 * By Sardor Tokhirov
 * Time-2:45 AM (GMT+5)
 */
@Entity
@Table(name = "out_of_contract_medicine_amounts_v2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutOfContractMedicineAmountV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;


    @Column(name = "year_month")
    private YearMonth yearMonth;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id")
    private User doctor;
}
