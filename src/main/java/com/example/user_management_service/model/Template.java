package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-4:53 PM (GMT+5)
 */

@Entity
@Table(name = "templates")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    private String name;

    private String diagnosis;

    @ElementCollection
    @CollectionTable(name = "template_preparation", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "preparation")
    private List<Preparation> preparations = new ArrayList<>();

    private String  note;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id")
    private User doctorId;

    private boolean saved = false;
}
