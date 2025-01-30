package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "sales_by_region")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SalesByRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", nullable = false)
    private Sales sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "direct_sales", nullable = false)
    private Integer directSales;

    @Column(name = "secondary_sales", nullable = false)
    private Integer secondarySales;

    @Column(name = "quote", nullable = false)
    private Long quote;

    @Column(name = "total_sales", nullable = false)
    private Integer totalSales;

    @Column(name = "sales_date", nullable = false)
    private LocalDate salesDate;


}