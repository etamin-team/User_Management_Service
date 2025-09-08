package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.GoalStatus;
import com.example.user_management_service.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-5:08 AM (GMT+5)
 */
@Entity
@Table(name = "manager_goals_v2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerGoalV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status = GoalStatus.APPROVED;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
    private User managerId;

    @OneToMany(mappedBy = "managerGoalV2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineQuoteV2> medicineQuoteV2s = new ArrayList<>();

    @OneToMany(mappedBy = "managerGoalV2", cascade = CascadeType.ALL)
    private List<FieldEnvQuoteV2> fieldEnvQuoteV2s;

    @OneToMany(mappedBy = "managerGoalV2", cascade = CascadeType.ALL)
    private List<MedAgentEnvV2> medAgentEnvs;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;


}
