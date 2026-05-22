package com.form.controller;

import com.form.dto.FormSubmissionDTO;
import com.form.model.FilledForm;
import com.form.model.Question;
import com.form.model.TemplateQuestion;
import com.form.service.FormService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*") // React'tan gelecek isteklere izin veriyoruz
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    // React'tan form doldurulup gönderildiğinde çalışacak API ucu
    @PostMapping("/submit")
    public ResponseEntity<?> submitForm(@RequestBody FormSubmissionDTO submissionDTO, Principal principal) {
        try {
            // Token'dan aktif giriş yapan teknisyenin adını cımbızlıyoruz
            String currentUsername = principal != null ? principal.getName() : "SYSTEM";

            FilledForm saved = formService.submitForm(submissionDTO, currentUsername);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Geçmiş formları listeleme ve filtreleme ucu
    @GetMapping("/submitted")
    public ResponseEntity<List<FilledForm>> getSubmittedForms(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String date) {

        java.time.LocalDate visitDate = null;
        if (date != null && !date.isEmpty()) {
            visitDate = java.time.LocalDate.parse(date);
        }
        return ResponseEntity.ok(formService.searchForms(companyId, visitDate));
    }

    // Tek bir formun detayını getirme ucu
    @GetMapping("/submitted/{id}")
    public ResponseEntity<com.form.dto.FormDetailsDTO> getFormDetails(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getFormDetails(id));
    }

    @PutMapping("/submitted/{id}")
    public ResponseEntity<?> updateForm(@PathVariable Long id, @RequestBody FormSubmissionDTO updateDTO, Principal principal) {
        try {
            // Token'dan aktif güncelleyen kişinin (admin veya teknisyen) adını alıyoruz
            String currentUsername = principal != null ? principal.getName() : "SYSTEM";

            FilledForm updated = formService.updateForm(id, updateDTO, currentUsername);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Şablona ait soruları getiren endpoint (Artık tamamen Servis tabanlı)
    @GetMapping("/templates/{templateId}/questions")
    public ResponseEntity<List<Question>> getTemplateQuestions( // <-- BURASI DEĞİŞTİ
                                                                @PathVariable Long templateId,
                                                                @RequestParam(required = false) String status) {

        List<Question> questions = formService.getQuestionsByTemplateId(templateId, status);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/submittedWithPage")
    public ResponseEntity<?> getSubmittedFormsWithPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        // Servisimizden sayfalanmış veriyi çekiyoruz
        Page<FilledForm> managedForms = formService.getPaginatedAndFilteredForms(page, size, search);

        // Spring'in Page nesnesi içerisinde hem o sayfanın verileri (content)
        // hem de toplam sayfa sayısı, toplam kayıt sayısı gibi frontend'in ihtiyaç duyacağı tüm meta veriler gelir.
        return ResponseEntity.ok(managedForms);
    }
}