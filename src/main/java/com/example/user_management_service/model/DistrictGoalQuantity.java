package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "district_goal_quantity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DistrictGoalQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @Column(name = "quote")
    private Long quote;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private ManagerGoal managerGoal;


    @ManyToOne
    @JoinColumn(name = "contract_district_amount_id", referencedColumnName = "id")
    private ContractDistrictAmount contractDistrictAmount;
}
