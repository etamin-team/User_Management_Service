package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "District_id", referencedColumnName = "id")
    private District District;

}
