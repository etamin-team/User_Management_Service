package com.example.user_management_service.model.v2;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "doctor_contract_visibility")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DoctorContractVisibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doctor_id", nullable = false, columnDefinition = "uuid")
    private UUID doctorId;

    @Column(name = "is_contract_visible", nullable = false)
    private boolean isContractVisible = true;
}