package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.District;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-6:28 AM (GMT+5)
 */
@Entity
@Table(name = "med_agent_env_v2")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedAgentEnvV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quote", nullable = false)
    private Long quote;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private District district;

    @OneToMany(mappedBy = "medAgentEnvV2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedAgentEnvAmountV2> medAgentEnvAmountV2s;

    @ManyToOne
    @JoinColumn(name = "goal_id", referencedColumnName = "goal_id")
    private ManagerGoalV2 managerGoalV2;
}
