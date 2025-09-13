package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Date-1/11/2025
 * By Sardor Tokhirov
 * Time-3:46 AM (GMT+5)
 */

@Entity
@Table(name = "conditions_to_preparate")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConditionsToPreparate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_percentage")
    private Double minPercentage;

    @Column(name = "min_percentage_val")
    private Double minPercentageVal;

    @Column(name = "max_percentage")
    private Double maxPercentage;

    @Column(name = "max_percentage_val")
    private Double maxPercentageVal;

    @ElementCollection
    @CollectionTable(name = "percentage_vals", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "percentage_vals")
    List<PercentageVal> percentageVals;


    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "su")
    private Double su;

    @Column(name = "sb")
    private Double sb;

    @Column(name = "gz")
    private Double gz;

    @Column(name = "kb")
    private Double kb;

    @Column(name = "recipt")
    private Double  recipt;


}
