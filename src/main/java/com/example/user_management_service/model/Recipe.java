package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-7:48 PM (GMT+5)
 */
@Entity
@Table(name = "recipes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "recipe_id", columnDefinition = "uuid")
    private UUID recipeId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_number_prefix")
    private String phoneNumberPrefix;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "comment")
    private String comment;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Column(name = "telegram_id")
    private Long telegramId;

    private ContractType contractType;

    @ElementCollection
    @CollectionTable(name = "recipe_preparation", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "preparation")
    private List<Preparation> preparations = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private User doctorId;

}
