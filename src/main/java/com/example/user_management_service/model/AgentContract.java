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
@Table(name = "agent_contracts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AgentContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "contract_medicine",
            joinColumns = @JoinColumn(name = "contract_id"),
            inverseJoinColumns = @JoinColumn(name = "medicine_id")
    )
    private List<Medicine> medicines;

    @OneToMany(mappedBy = "agentContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineWithQuantity> medicinesWithQuantities;

    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "contract_status")
    private String contractStatus;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "quota_60")
    private Double quota60;

    @Column(name = "quota_75_90")
    private Double quota75To90;

    @Column(name = "su")
    private Double su;

    @Column(name = "sb")
    private Double sb;

    @Column(name = "gz")
    private Double gz;

    @Column(name = "kb")
    private Double kb;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "agent_id", referencedColumnName = "user_id")
    private User medAgent;

}
