package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "medicine_with_quantity_doctor")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineWithQuantityDoctor {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @ManyToOne
        @JoinColumn(name = "medicine_id")
        private Medicine medicine;

        @Column(name = "quote")
        private Integer quote;

        @ManyToOne
        @JoinColumn(name = "contract_id")
        private Contract doctorContract;
    }