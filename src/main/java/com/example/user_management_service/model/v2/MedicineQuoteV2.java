package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.Medicine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-8/21/2025
 * By Sardor Tokhirov
 * Time-2:56 PM (GMT+5)
 */
@Entity
@Table(name = "medicine_quote_v2")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineQuoteV2 {

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
    private ManagerGoalV2 managerGoalV2;

}
