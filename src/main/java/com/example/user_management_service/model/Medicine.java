package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Date-12/28/2024
 * By Sardor Tokhirov
 * Time-3:12 PM (GMT+5)
 */
@Entity
@Table(name = "medicines")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    private Long price;

    private Integer quantity;

    private Integer prescription;

    private String volume;

    private Double limit;
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MedicineQuantity type;
}

enum MedicineQuantity {
    ITEM, ML, GR
}

