package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Date-2/25/2025
 * By Sardor Tokhirov
 * Time-6:16 AM (GMT+5)
 */
@Entity
@Table(name = "sales_report")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SalesReport {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate reportDate;
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private Long written ;
    private Long allowed ;
    private Long sold ;
    private boolean saved = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType contractType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medicine_id" , referencedColumnName = "id")
    private Medicine medicine;

    @ManyToOne
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    private Region region;

}
