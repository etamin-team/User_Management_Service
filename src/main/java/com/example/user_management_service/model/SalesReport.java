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
    private LocalDate startDate;
    private LocalDate endDate;
    private Long written ;
    private Long allowed ;
    private Long sold ;


    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @ManyToOne
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    private Region region;

}
