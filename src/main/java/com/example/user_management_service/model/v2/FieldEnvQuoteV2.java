package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.Field;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Date-8/22/2025
 * By Sardor Tokhirov
 * Time-6:45 PM (GMT+5)
 */
@Entity
@Table(name = "field_env_quote_v2")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldEnvQuoteV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Field field;

    private Long quote;

    @ManyToOne
    @JoinColumn(name = "goal_id", referencedColumnName = "goal_id")
    private ManagerGoalV2 managerGoalV2;
}
