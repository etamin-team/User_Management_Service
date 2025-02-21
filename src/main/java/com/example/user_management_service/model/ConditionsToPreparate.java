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
    private Integer minPercentage;

    @Column(name = "min_percentage_val")
    private Integer minPercentageVal;

    @Column(name = "max_percentage")
    private Integer maxPercentage;

    @Column(name = "max_percentage_val")
    private Integer maxPercentageVal;

    @ElementCollection
    @CollectionTable(name = "percentage_vals", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "percentage_vals")
    List<PercentageVal> percentageVals;


    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "su")
    private Integer su;

    @Column(name = "sb")
    private Integer sb;

    @Column(name = "gz")
    private Integer gz;

    @Column(name = "kb")
    private Integer kb;


}
