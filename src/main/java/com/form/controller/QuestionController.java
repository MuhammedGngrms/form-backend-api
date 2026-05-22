package com.form.controller;

import com.form.model.TemplateQuestion;
import com.form.model.FormTemplate;
import com.form.repository.TemplateQuestionRepository;
import com.form.repository.FormTemplateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    private final TemplateQuestionRepository questionRepository;
    private final FormTemplateRepository templateRepository;

    public QuestionController(TemplateQuestionRepository questionRepository, FormTemplateRepository templateRepository) {
        this.questionRepository = questionRepository;
        this.templateRepository = templateRepository;
    }

    // 1. Yeni Soru Ekle
    @PostMapping
    public ResponseEntity<TemplateQuestion> addQuestion(@RequestBody TemplateQuestion newQuestion) {
        FormTemplate template = templateRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Şablon bulunamadı"));
        newQuestion.setTemplate(template);
        newQuestion.setStatus("S"); // Güvenceye alıyoruz
        return ResponseEntity.ok(questionRepository.save(newQuestion));
    }

    // 3. Soru Sil
    @DeleteMapping("/{id}")
    public ResponseEntity<TemplateQuestion> deleteQuestion(@PathVariable Long id) {
        TemplateQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soru bulunamadı: " + id));

        question.setStatus("D"); // Veriyi silme, durumunu 'D' yap
        return ResponseEntity.ok(questionRepository.save(question));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateQuestion> updateQuestion(@PathVariable Long id, @RequestBody TemplateQuestion questionDetails) {
        TemplateQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soru bulunamadı: " + id));

        question.setQuestionText(questionDetails.getQuestionText());
        question.setInputType(questionDetails.getInputType());
        question.setOrderIndex(questionDetails.getOrderIndex());

        // KRİTİK EKSİK: Şalterin çalışması için durum bilgisini de içeri alıyoruz
        if (questionDetails.getStatus() != null) {
            question.setStatus(questionDetails.getStatus());
        }

        return ResponseEntity.ok(questionRepository.save(question));
    }


}