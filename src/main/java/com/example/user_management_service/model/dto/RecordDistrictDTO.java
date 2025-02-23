package com.example.user_management_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Date-2/22/2025
 * By Sardor Tokhirov
 * Time-7:28 PM (GMT+5)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDistrictDTO {


    List<StatsEmployeeDTO> employeeStatsList;
    List<RecordStatsEmployeeFactDTO> recordStatsEmployeeFactList;
    List<RecordWorkPlaceStatsDTO> recordWorkPlaceStatsDTOList;
}
