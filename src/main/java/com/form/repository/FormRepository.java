package com.form.repository;

import com.form.model.FilledForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends JpaRepository<FilledForm, Long> {

    // Hem firma adına hem de teknisyen adına göre büyük/küçük harf duyarsız arama yapan ve sayfalayan dev metot
    @Query("SELECT f FROM FilledForm f WHERE " +
           "LOWER(f.company.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.technicianName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<FilledForm> findBySearchTerm(@Param("search") String searchTerm, Pageable pageable);
}