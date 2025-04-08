package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-4/7/2025
 * By Sardor Tokhirov
 * Time-7:34 AM (GMT+5)
 */
@Entity
@Table(name = "contract_medicine_manager_amounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractMedicineManagerAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;
}
