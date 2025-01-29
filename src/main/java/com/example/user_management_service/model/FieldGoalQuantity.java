package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "field_goal_quantity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldGoalQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Field field ;

    @Column(name = "quote")
    private Integer quote;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private ManagerGoal managerGoal;

    @ManyToOne
    @JoinColumn(name = "contract_field_amount_id", referencedColumnName = "id")
    private ContractFieldAmount contractFieldAmount;
}