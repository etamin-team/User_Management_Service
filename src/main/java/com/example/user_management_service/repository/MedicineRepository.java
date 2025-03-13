package com.example.user_management_service.repository;

import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Date-12/29/2024
 * By Sardor Tokhirov
 * Time-12:09 PM (GMT+5)
 */

@Repository
public interface MedicineRepository  extends JpaRepository<Medicine, Long> {

    @Query("SELECT m FROM Medicine m JOIN m.inn i WHERE LOWER(i) IN :inns")
    List<Medicine> findByInn(@Param("inns") List<String> inns);

    @Query("SELECT m FROM Medicine m JOIN m.inn i " +
            "WHERE LOWER(i) IN :inns " +
            "GROUP BY m " +
            "HAVING COUNT(DISTINCT i) = :size")
    List<Medicine> findByAllInn(@Param("inns") List<String> inns, @Param("size") long size);


    @Query("""
    SELECT u FROM Medicine u 
    WHERE u.status = 'ACTIVE'
    ORDER BY u.createdDate DESC
""")
    List<Medicine> findAllSortByCreatedDate();

    @Query("""
    SELECT u FROM Medicine u 
    WHERE u.status = 'ACTIVE'
    ORDER BY u.createdDate DESC
""")
    Page<Medicine> findAllSortByCreatedDatePageable(Pageable pageable);


}
