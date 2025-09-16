//package com.example.user_management_service.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDate;
//import java.util.List;
//
///**
// * Date-12/26/2024
// * By Sardor Tokhirov
// * Time-4:43 AM (GMT+5)
// */
//
//@Entity
//@Table(name = "agent_goals")
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//public class AgentGoal {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "goal_id")
//    private Long id;
//
//    @OneToMany(mappedBy = "agentGoal", cascade = CascadeType.ALL)
//    private List<FieldWithQuantity> fieldWithQuantities;
//
//    @OneToMany(mappedBy = "agentGoal", cascade = CascadeType.ALL)
//    private List<MedicineAgentGoalQuantity> medicineAgentGoalQuantities;
//
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private GoalStatus status = GoalStatus.APPROVED;
//
//    @ManyToOne
//    @JoinColumn(name = "contract_district_amount_id", referencedColumnName = "id")
//    private DistrictGoalQuantity districtGoalQuantity;
//
//
//    @Column(name = "created_at")
//    private LocalDate createdAt;
//
//    @Column(name = "start_date")
//    private LocalDate startDate;
//
//    @Column(name = "end_date")
//    private LocalDate endDate;
//
//    @ManyToOne
//    @JoinColumn(name = "agent_id", referencedColumnName = "user_id")
//    private User medAgent;
//
//    @ManyToOne
//    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
//    private User manager;
//
//    @ManyToOne
//    @JoinColumn(name = "manager_goal_id", referencedColumnName = "goal_id")
//    private ManagerGoal managerGoal;
//
//}
