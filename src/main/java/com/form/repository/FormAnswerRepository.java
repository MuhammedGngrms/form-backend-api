package com.form.repository;

import com.form.model.FormAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FormAnswerRepository extends JpaRepository<FormAnswer, Long> {
    // Varsa sadece findByFilledFormId gibi özel metotların kalmalı.
    List<FormAnswer> findByFilledFormId(Long filledFormId);

    // ELLE YAZILAN deleteAll BURADA ASLA OLMAMALI!
}