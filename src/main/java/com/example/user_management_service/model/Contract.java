package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToMany
    @JoinTable(
            name = "contract_medicine",
            joinColumns = @JoinColumn(name = "contract_id"),
            inverseJoinColumns = @JoinColumn(name = "medicine_id")
    )
    private List<Medicine> medicines;
    @Column(name = "contract_date")
    private String contractDate;

    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "contract_status")
    private String contractStatus;

    @Column(name = "total_amount")
    private Double totalAmount;
}
