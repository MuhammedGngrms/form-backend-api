package com.form.repository;

import com.form.model.FilledSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilledSectionRepository extends JpaRepository<FilledSection, Long> {
    List<FilledSection> findByFilledFormId(Long filledFormId);
}