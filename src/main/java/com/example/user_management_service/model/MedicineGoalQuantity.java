package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "medicine_goal_quantity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineGoalQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "quote")
    private Long quote;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private ManagerGoal managerGoal;

    @ManyToOne
    @JoinColumn(name = "contract_medicine_amount_id", referencedColumnName = "id")
    private ContractMedicineAmount contractMedicineAmount;

}
