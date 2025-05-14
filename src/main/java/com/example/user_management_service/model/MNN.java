package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date-3/8/2025
 * By Sardor Tokhirov
 * Time-8:24 AM (GMT+5)
 */

@Entity
@Table(name = "mnn")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MNN {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 4000)
    private String name;
}
