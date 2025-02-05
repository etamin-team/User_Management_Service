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
    @Column(name = "goal_id")
    private Long goalId;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
    private User managerId;



    @OneToMany(mappedBy = "managerGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldGoalQuantity> fieldGoalQuantities;

    @OneToMany(mappedBy = "managerGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineGoalQuantity> medicineGoalQuantities;

    @OneToMany(mappedBy = "managerGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistrictGoalQuantity> districtGoalQuantities;


    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    private User admin;
}
