package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.FieldWithQuantity;
import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:36 AM (GMT+5)
 */

@Entity
@Table(name = "med_agent_goals_v2")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedAgentGoalV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status = GoalStatus.APPROVED;



    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "goal_id", referencedColumnName = "goal_id")
    private List<DoctorContractV2> doctorContractV2s = new ArrayList<>();

    @OneToMany(mappedBy = "medAgentGoalV2", cascade = CascadeType.ALL)
    private List<FieldEnvV2> fieldEnvV2s;


    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "agent_id", referencedColumnName = "user_id")
    private User medAgent;


}
