package com.form.repository;
import com.form.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Durumuna göre şirketleri getir (Örn: Sadece 'S' olanlar)
    List<Company> findByStatus(String status);
}