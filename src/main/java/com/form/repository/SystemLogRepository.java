package com.form.repository;

import com.form.model.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    // Tüm logları sayfalı ve en yeniden eskiye getirir
    Page<SystemLog> findAllByOrderByIdDesc(Pageable pageable);

    // Seçilen aksiyona (örn: "FORM_OLUŞTURMA") göre filtreleyip sayfalı getirir
    Page<SystemLog> findByActionOrderByIdDesc(String action, Pageable pageable);
}