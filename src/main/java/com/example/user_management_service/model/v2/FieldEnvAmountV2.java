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
 * Time-4:58 AM (GMT+5)
 */
@Entity
@Table(name = "field_env_amounts_v2")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldEnvAmountV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;


    @Column(name = "year_month")
    private YearMonth yearMonth;

    @ManyToOne
    @JoinColumn(name = "field_env_id")
    private FieldEnvV2 fieldEnvV2;
}
