package com.form.repository;
import com.form.model.FilledForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
public interface FilledFormRepository extends JpaRepository<FilledForm, Long> {
    List<FilledForm> findByCompanyId(Long companyId);

    @Query("SELECT f FROM FilledForm f WHERE " +
            "(:companyId IS NULL OR f.company.id = :companyId) AND " +
            "(:visitDate IS NULL OR f.visitDate = :visitDate) " +
            "ORDER BY f.visitDate DESC")
    List<FilledForm> filterForms(@Param("companyId") Long companyId,
                                 @Param("visitDate") LocalDate visitDate);
}