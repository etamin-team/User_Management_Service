package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Preparation {


    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private String amount;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "times_in_day")
    private Integer timesInDay;

    @Column(name = "days")
    private Integer days;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PreparationType type;


    @ManyToOne()
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    private Medicine medicine;
}

