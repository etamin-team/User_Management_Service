package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Date-12/26/2024
 * By Sardor Tokhirov
 * Time-4:43 AM (GMT+5)
 */

@Entity
@Table(name = "contracts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;



}
