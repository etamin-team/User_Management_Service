package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "regions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "region_seq_2")
    @SequenceGenerator(name = "region_seq_2", sequenceName = "region_seq_2", allocationSize = 1, initialValue = 100)
    private Long id;


    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "name_uz_c", nullable = false)
    private String nameUzCyrillic;

    @Column(name = "name_uz_l", nullable = false)
    private String nameUzLatin;

    @Column(name = "name_ru", nullable = false)
    private String nameRussian;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private List<District> districts;

}
