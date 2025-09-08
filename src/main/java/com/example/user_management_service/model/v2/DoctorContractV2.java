package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-2:52 AM (GMT+5)
 */
@Entity
@Table(name = "doctor_contracts_v2")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DoctorContractV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status = GoalStatus.PENDING_REVIEW;


    @OneToMany(mappedBy = "doctorContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineWithQuantityDoctorV2> medicineWithQuantityDoctorV2s;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType contractType;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id")
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User createdBy;

}
