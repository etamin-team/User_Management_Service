package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.Medicine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Date-9/9/2025
 * By Sardor Tokhirov
 * Time-5:44 AM (GMT+5)
 */
@Entity
@Table(name = "med_agent_medicine_quote_v2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedAgentMedicineQuoteV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "quote")
    private Long quote;

    @ManyToOne
    @JoinColumn(name = "goal_id", referencedColumnName = "goal_id")
    private MedAgentGoalV2 medAgentGoalV2;
}
