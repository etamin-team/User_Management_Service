package com.example.user_management_service.service;

import com.example.user_management_service.model.Field;
import com.example.user_management_service.model.dto.RecordDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Date-2/22/2025
 * By Sardor Tokhirov
 * Time-7:22 PM (GMT+5)
 */
@Service
public class DashboardService {


    public List<RecordDTO> getFilteredRecords(Long regionId, Long districtId, Long workplaceId, Field field, String query, Long medicineId, LocalDate startDate, LocalDate endDate) {
        if (medicineId!=null){
            return null;
        }else if (query!=null){

        }else if (field!=null){
            return null;
        }else if (workplaceId!=null){
            return null;
        }else if(districtId!=null){
            return null;
        }else if (regionId!=null){
            return null;
        }

        return null;
    }
}
