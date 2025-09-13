package com.example.user_management_service.model.v2;

import com.example.user_management_service.model.Region;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

/**
 * Date-9/13/2025
 * By Sardor Tokhirov
 * Time-6:59 PM (GMT+5)
 */

@Entity
@Table(name = "report_savings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSaving {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "year_month")
    private YearMonth yearMonth;

    private boolean isSaved;

    @ManyToOne
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    private Region region;

}
