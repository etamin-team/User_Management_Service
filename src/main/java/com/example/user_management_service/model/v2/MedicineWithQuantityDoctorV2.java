package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.Medicine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-2:51 AM (GMT+5)
 */
@Entity
@Table(name = "medicine_with_quantity_doctor_v2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineWithQuantityDoctorV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "quote")
    private Long quote;

    @OneToMany(mappedBy = "medicineWithQuantityDoctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContractMedicineDoctorAmountV2> contractMedicineDoctorAmountV2s;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private DoctorContractV2 doctorContract;
}