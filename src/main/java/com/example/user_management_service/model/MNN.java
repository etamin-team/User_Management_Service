package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-3/8/2025
 * By Sardor Tokhirov
 * Time-8:24 AM (GMT+5)
 */

@Entity
@Table(name = "mnn")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MNN {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 4000)
    private String name;

    @Column(length = 4000,name = "latin_name")
    private String latinName;

    @Column(length = 4000)
    private String combination;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PreparationType type;

    private String dosage;


    @Column(length = 4000)
    private String wm_ru;

    @Column(length = 4000,name = "pharmacotherapeutic_group")
    private String pharmacotherapeuticGroup ;


}
