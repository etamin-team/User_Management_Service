package com.example.user_management_service.model.dto;
import com.example.user_management_service.model.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldGoalQuantityDTO {
    private Long id;
    private Field fieldName;
    private Integer quote;
    private Long managerGoalId;
}