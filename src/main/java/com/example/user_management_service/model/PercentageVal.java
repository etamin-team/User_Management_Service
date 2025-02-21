package com.example.user_management_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Date-2/21/2025
 * By Sardor Tokhirov
 * Time-2:18 PM (GMT+5)
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PercentageVal {

    @Column(name = "min_percentage")
    private Integer minPercentage;

    @Column(name = "min_percentage_val")
    private Integer minPercentageVal;

    @Column(name = "max_percentage")
    private Integer maxPercentage;

    @Column(name = "max_percentage_val")
    private Integer maxPercentageVal;

}
