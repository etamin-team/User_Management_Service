package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "districts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "name_uz_c", nullable = false)
    private String nameUzCyrillic;

    @Column(name = "name_uz_l", nullable = false)
    private String nameUzLatin;

    @Column(name = "name_ru", nullable = false)
    private String nameRussian;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

}
