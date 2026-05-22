package com.form.repository;

import com.form.model.TemplateQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TemplateQuestionRepository extends JpaRepository<TemplateQuestion, Long> {

    // Admin paneli için: Bir şablona ait TÜM soruları sırasıyla getir (Aktif/Pasif dahil)
    List<TemplateQuestion> findByTemplateIdOrderByOrderIndexAsc(Long templateId);

    // Saha ekranı için: Bir şablona ait sadece AKTİF soruları sırasıyla getir
    List<TemplateQuestion> findByTemplateIdAndStatusOrderByOrderIndexAsc(Long templateId, String status);
}