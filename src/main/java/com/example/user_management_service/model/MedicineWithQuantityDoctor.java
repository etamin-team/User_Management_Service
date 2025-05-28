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

        @ManyToOne(cascade = CascadeType.REMOVE)
        @JoinColumn(name = "medicine_id")
        private Medicine medicine;

        @Column(name = "quote")
        private Long quote;

        @Column(name = "correction")
        private Long correction;

        @ManyToOne
        @JoinColumn(name = "contract_medicine_manager_amount_id", referencedColumnName = "id")
        private ContractMedicineManagerAmount contractMedicineManagerAmount;

        @ManyToOne
        @JoinColumn(name = "contract_medicine_med_agent_amount_id", referencedColumnName = "id")
        private ContractMedicineMedAgentAmount contractMedicineMedAgentAmount;

        @ManyToOne
        @JoinColumn(name = "contract_medicine_doctor_amount_id", referencedColumnName = "id")
        private ContractMedicineDoctorAmount contractMedicineDoctorAmount;


        @ManyToOne
        @JoinColumn(name = "contract_id")
        private Contract doctorContract;
}