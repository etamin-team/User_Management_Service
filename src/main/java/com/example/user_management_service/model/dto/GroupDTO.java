package com.example.user_management_service.model.dto;

import lombok.*;

import java.util.List;

/**
 * Date-6/9/2025
 * By Sardor Tokhirov
 * Time-8:26 AM (GMT+5)
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private Long groupId;
    private String groupName;
    private UserDTO users;
}
