package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contract_field_amounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractFieldAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;
}