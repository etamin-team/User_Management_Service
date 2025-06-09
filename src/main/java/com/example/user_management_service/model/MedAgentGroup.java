package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Date-6/9/2025
 * By Sardor Tokhirov
 * Time-7:01 AM (GMT+5)
 */
@Entity
@Table(name = "agent_groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedAgentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_group_id")
    private Long agentGroupId;

    @Column(name = "group_name")
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
