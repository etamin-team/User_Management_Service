package com.example.user_management_service.repository;

import com.example.user_management_service.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Date-12/24/2024
 * By Sardor Tokhirov
 * Time-5:23 PM (GMT+5)
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query("SELECT t FROM Template t WHERE t.doctorId.userId=:doctorId ORDER BY t.diagnosis ASC")
    List<Template> findAllByDoctorIdAsc(@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE t.doctorId.userId =:doctorId")
    List<Template> findAllByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE t.saved = true AND t.doctorId.userId=:doctorId")
    List<Template> findBySavedTrue(@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE t.saved = false AND t.doctorId.userId=:doctorId")
    List<Template> findBySavedFalse(@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE  t.saved = true AND t.doctorId.userId =:doctorId ORDER BY t.diagnosis ASC")
    List<Template> findBySavedTrueOrderByDiagnosisAsc(@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE  t.saved = false AND t.doctorId.userId =:doctorId ORDER BY t.diagnosis ASC")
    List<Template> findBySavedFalseOrderByDiagnosisAsc(@Param("doctorId") UUID doctorId);

    // Methods for searchText filtering
    @Query("SELECT t FROM Template t WHERE t.saved = true AND (t.name LIKE %:searchText% OR t.diagnosis LIKE %:searchText%) AND t.doctorId.userId=:doctorId")
    List<Template> findBySavedTrueAndSearchText(@Param("searchText") String searchText,@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE t.saved = true AND (t.name LIKE %:searchText% OR t.diagnosis LIKE %:searchText%) AND t.doctorId.userId=:doctorId  ORDER BY t.diagnosis ASC")
    List<Template> findBySavedTrueAndSearchTextOrderByDiagnosisAsc(@Param("searchText") String searchText,@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE t.saved = false AND (t.name LIKE %:searchText% OR t.diagnosis LIKE %:searchText%) AND t.doctorId.userId=:doctorId")
    List<Template> findBySavedFalseAndSearchText(@Param("searchText") String searchText,@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE t.saved = false AND (t.name LIKE %:searchText% OR t.diagnosis LIKE %:searchText%) AND t.doctorId.userId=:doctorId ORDER BY t.diagnosis ASC")
    List<Template> findBySavedFalseAndSearchTextOrderByDiagnosisAsc(@Param("searchText") String searchText,@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE (t.name LIKE %:searchText% OR t.diagnosis LIKE %:searchText%) AND t.doctorId.userId=:doctorId")
    List<Template> findAllBySearchText(@Param("searchText") String searchText,@Param("doctorId") UUID doctorId);

    @Query("SELECT t FROM Template t WHERE (t.name LIKE %:searchText% OR t.diagnosis LIKE %:searchText%) AND t.doctorId.userId=:doctorId ORDER BY t.diagnosis ASC")
    List<Template> findAllBySearchTextOrderByDiagnosisAsc(@Param("searchText") String searchText,@Param("doctorId") UUID doctorId);
}
