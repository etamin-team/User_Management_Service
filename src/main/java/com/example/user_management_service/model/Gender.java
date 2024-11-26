package com.example.user_management_service.model;

/**
 * Date-11/19/2024
 * By Sardor Tokhirov
 * Time-4:35 PM (GMT+5)
 */
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "genders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gender {

    @Id
    @Column(name = "gender_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gender_sequence")
    @SequenceGenerator(name = "gender_sequence", sequenceName = "gender_sequence_name", allocationSize = 1)
    private Integer genderId;

    @Column(name = "gender_name", unique = true)
    private String genderName;
}