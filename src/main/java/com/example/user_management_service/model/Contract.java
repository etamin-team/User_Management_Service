package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Date-12/26/2024
 * By Sardor Tokhirov
 * Time-4:43 AM (GMT+5)
 */

@Entity
@Table(name = "contracts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status = GoalStatus.PENDING_REVIEW;

    @Column(name = "total_amount")
    private Double totalAmount;

    @OneToMany(mappedBy = "doctorContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineWithQuantityDoctor> medicineWithQuantityDoctors;

    @OneToMany(mappedBy = "doctorContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutOfContractMedicineAmount> outOfContractMedicineAmounts;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id")
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "agent_contract_id", referencedColumnName = "contract_id")
    private AgentContract agentContract;
}
