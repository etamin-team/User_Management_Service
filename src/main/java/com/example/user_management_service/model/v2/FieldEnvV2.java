package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.Field;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-4:54 AM (GMT+5)
 */
@Entity
@Table(name = "field_envs_v2")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldEnvV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Field field;

    private Long quote;

    @OneToMany(mappedBy = "fieldEnvV2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldEnvAmountV2> fieldEnvAmountV2;

    @ManyToOne
    @JoinColumn(name = "goal_id", referencedColumnName = "goal_id")
    private MedAgentGoalV2 medAgentGoalV2;
}