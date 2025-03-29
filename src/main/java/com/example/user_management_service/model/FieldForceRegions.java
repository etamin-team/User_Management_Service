package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Date-3/25/2025
 * By Sardor Tokhirov
 * Time-2:04 PM (GMT+5)
 */
@Entity
@Table(name = "field_force_regions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldForceRegions {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "field_force_regions_seq")
    @SequenceGenerator(name = "field_force_regions_seq", sequenceName = "field_force_regions_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ElementCollection
    @CollectionTable(name = "field_force_regions_list", joinColumns = @JoinColumn(name = "field_force_region_id"))
    @Column(name = "region_id")
    private List<Long> regionIds;

}
