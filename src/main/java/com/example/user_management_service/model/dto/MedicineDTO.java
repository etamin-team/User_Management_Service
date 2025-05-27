package com.example.user_management_service.model.dto;

import com.example.user_management_service.model.MNN;
import com.example.user_management_service.model.MedicineQuantity;
import com.example.user_management_service.model.PreparationType;
import com.example.user_management_service.model.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Date-5/27/2025
 * By Sardor Tokhirov
 * Time-5:46 AM (GMT+5)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicineDTO {

    private Long id;

    private String name;

    private String nameUzCyrillic;

    private String nameUzLatin;

    private String nameRussian;

    private Status status;

    private LocalDateTime createdDate;


    private String imageUrl;


    private List<Long> mnn;

    private Long cip;

    private Integer quantity;

    private Integer noMore;

    private Double prescription;


    private MedicineQuantity volume;



    private PreparationType type;


    private Double recipePercentage;

    private Double recipeLimit;
    private Integer recipeBall;

    private Double suPercentage;

    private Double suLimit;

    private Integer suBall;

    private Double sbPercentage;

    private Double sbLimit;

    private Integer sbBall;

    private Double gzPercentage;

    private Double gzLimit;

    private Integer gzBall;

    private Double kbPercentage;

    private Double kbLimit;

    private Integer kbBall;
}
