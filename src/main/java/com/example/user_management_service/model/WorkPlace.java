package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-12:11 PM (GMT+5)
 */
@Entity
@Table(name = "work_places")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workplace_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address",length = 600)
    private String address;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING) // Store the enum as a string in the database
    @Column(name = "medical_institution_type", nullable = false)
    private MedicalInstitutionType medicalInstitutionType;

    @ManyToOne
    @JoinColumn(name = "chief_id", referencedColumnName = "user_id")
    private User chiefDoctor;

    @ManyToOne
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private District district;
}
