package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "manager_goals")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
    private User managerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status = GoalStatus.PENDING_REVIEW;

    @ElementCollection
    private List<Field> fields;

    @ManyToMany
    @JoinTable(
            name = "manager_goal_medicines",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "medicine_id")
    )
    private List<Medicine> medicines;

    @ManyToMany
    @JoinTable(
            name = "manager_goal_districts",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "district_id")
    )
    private List<District> districts;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
