package com.example.user_management_service.model.v2;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-3:10 PM (GMT+5)
 */
@Entity
@Table(name = "med_agent_env_amount_v2")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedAgentEnvAmountV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "year_month")
    private YearMonth yearMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "med_agent_env_v2_id")
    private MedAgentEnvV2 medAgentEnvV2;
}
