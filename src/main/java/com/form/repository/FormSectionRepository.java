package com.form.repository;

import com.form.model.FormSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormSectionRepository extends JpaRepository<FormSection, Long> {
}