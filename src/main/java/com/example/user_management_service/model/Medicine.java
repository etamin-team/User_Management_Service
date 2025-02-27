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

    @Column(nullable = false)
    private String name;

    @Column(name = "name_uz_c")
    private String nameUzCyrillic;

    @Column(name = "name_uz_l")
    private String nameUzLatin;

    @Column(name = "name_ru")
    private String nameRussian;


    @Column(name = "image_url")
    private String imageUrl;

    private String inn;

    private Long cip;

    private Integer quantity;

    private Integer prescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "volume")
    private MedicineQuantity volume;


    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PreparationType type;


    @Column(name = "su_percentage")
    private Double suPercentage;

    @Column(name = "su_limit")
    private Double suLimit;

    @Column(name = "su_ball")
    private Integer suBall;

    @Column(name = "sb_percentage")
    private Double sbPercentage;

    @Column(name = "sb_limit")
    private Double sbLimit;

    @Column(name = "sb_ball")
    private Integer sbBall;

    @Column(name = "gz_percentage")
    private Double gzPercentage;

    @Column(name = "gz_limit")
    private Double gzLimit;

    @Column(name = "gz_ball")
    private Integer gzBall;

    @Column(name = "kb_percentage")
    private Double kbPercentage;

    @Column(name = "kb_limit")
    private Double kbLimit;

    @Column(name = "kb_ball")
    private Integer kbBall;
}


