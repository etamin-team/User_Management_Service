package com.example.user_management_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "field_with_quantity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldWithQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Field field ;

    @Column(name = "quote")
    private Integer quote;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private AgentContract agentContract;

    @ManyToOne
    @JoinColumn(name = "contract_field_amount_id", referencedColumnName = "id")
    private ContractFieldAmount contractFieldAmount;
}