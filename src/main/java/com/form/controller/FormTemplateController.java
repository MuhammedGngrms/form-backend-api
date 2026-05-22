package com.form.controller;

import com.form.model.FormTemplate;
import com.form.repository.FormTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms/templates")
@CrossOrigin("*")
public class FormTemplateController {

    @Autowired
    private FormTemplateRepository templateRepository;

    // Tüm aktif şablonları listele (NewForm'daki seçim kutusu için)
    @GetMapping
    public List<FormTemplate> getAllTemplates() {
        return templateRepository.findAll(); 
    }

    // Seçilen şablonun tüm detaylarını (Bölümler ve Sorular dahil) getir
    @GetMapping("/{id}")
    public FormTemplate getTemplateById(@PathVariable Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şablon bulunamadı"));
    }

    // Yeni şablon oluştur (Admin paneli için)
    @PostMapping
    public FormTemplate createTemplate(@RequestBody FormTemplate template) {
        // İlişkileri Java tarafında bağlamak önemli
        if (template.getSections() != null) {
            template.getSections().forEach(section -> {
                section.setTemplate(template);
                if (section.getQuestions() != null) {
                    // İŞTE DÜZELTİLEN SATIR: section yerine question.setSection olacak
                    section.getQuestions().forEach(question -> question.setSection(section));
                }
            });
        }
        return templateRepository.save(template);
    }

    @PutMapping("/{id}")
    public FormTemplate updateTemplate(@PathVariable Long id, @RequestBody FormTemplate updatedTemplate) {
        FormTemplate existingTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şablon bulunamadı"));

        // Eskileri silip yenilerini üzerine yazmanın en garanti yolu:
        existingTemplate.getSections().clear();

        existingTemplate.setName(updatedTemplate.getName());
        existingTemplate.setTitle(updatedTemplate.getTitle());

        if (updatedTemplate.getSections() != null) {
            updatedTemplate.getSections().forEach(section -> {
                section.setTemplate(existingTemplate);
                if (section.getQuestions() != null) {
                    section.getQuestions().forEach(question -> question.setSection(section));
                }
                existingTemplate.getSections().add(section);
            });
        }
        return templateRepository.save(existingTemplate);
    }
}