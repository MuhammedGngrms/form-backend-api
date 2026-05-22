package com.form.service;

import com.form.dto.AnswerDTO;
import com.form.dto.FormDetailsDTO;
import com.form.dto.FormSubmissionDTO;
import com.form.dto.SectionDTO;
import com.form.model.*;
import com.form.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FormService {

    private final FilledFormRepository filledFormRepository;
    private final FormAnswerRepository formAnswerRepository;
    private final CompanyRepository companyRepository;
    private final FormTemplateRepository formTemplateRepository;
    //private final TemplateQuestionRepository questionRepository;
    private QuestionRepository questionRepository;
    private final FormRepository formRepository;
    private final SystemLogService logService;
    private FormSectionRepository formSectionRepository;
    private FilledSectionRepository filledSectionRepository;

    public FormService(FilledFormRepository filledFormRepository,
                       FormAnswerRepository formAnswerRepository,
                       CompanyRepository companyRepository,
                       FormTemplateRepository formTemplateRepository,
                       QuestionRepository questionRepository,
                       FormRepository formRepository,
                       SystemLogService logService,
                       FormSectionRepository formSectionRepository,
                       FilledSectionRepository filledSectionRepository) {
        this.filledFormRepository = filledFormRepository;
        this.formAnswerRepository = formAnswerRepository;
        this.companyRepository = companyRepository;
        this.formTemplateRepository = formTemplateRepository;
        this.questionRepository = questionRepository;
        this.formRepository = formRepository;
        this.logService = logService;
        this.formSectionRepository = formSectionRepository;
        this.filledSectionRepository = filledSectionRepository;
    }

    // React ilk açıldığında formu çizmek için soruları bu metodla çekeceğiz
    public List<Question> getQuestionsForTemplate(Long templateId) {

        // Yeni hiyerarşiye (Bölüm -> Şablon) ve yeni sıralama adına (displayOrder) göre çekiyoruz
        return questionRepository.findBySectionTemplateIdOrderByDisplayOrderAsc(templateId);

    }

    // Tablet üzerinden form doldurulup "Kaydet"e basıldığında burası çalışacak
    // Tablet üzerinden form doldurulup "Kaydet"e basıldığında burası çalışacak
    @Transactional
    public FilledForm submitForm(FormSubmissionDTO submissionDTO, String currentUsername) {
        // 1. Şirket ve Şablonun veritabanında olup olmadığını kontrol et
        Company company = companyRepository.findById(submissionDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı!"));

        FormTemplate template = formTemplateRepository.findById(submissionDTO.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Form şablonu bulunamadı!"));

        // 2. Ana form kaydını oluştur
        FilledForm filledForm = new FilledForm();
        filledForm.setCompany(company);
        filledForm.setTemplate(template);
        filledForm.setTechnicianName(submissionDTO.getTechnicianName());

        // Ekranda seçilen tarihi kaydet (Eğer boş geldiyse bugünü baz al)
        if (submissionDTO.getVisitDate() != null && !submissionDTO.getVisitDate().isEmpty()) {
            filledForm.setVisitDate(java.time.LocalDate.parse(submissionDTO.getVisitDate()));
        } else {
            filledForm.setVisitDate(java.time.LocalDate.now());
        }

        filledForm.setTechnicianSignatureUrl(submissionDTO.getTechnicianSignatureUrl());
        filledForm.setCompanyOfficialSignatureUrl(submissionDTO.getCompanyOfficialSignatureUrl());
        filledForm.setGeneralNotes(submissionDTO.getGeneralNotes());
        filledForm.setCompanyOfficialName(submissionDTO.getCompanyOfficialName());

        // Veritabanına kaydet (JPA Auditing burada created_user ve created_date alanlarını otomatik dolduracak)
        FilledForm savedForm = filledFormRepository.save(filledForm);

        // --- YENİ EKLENEN ADIM: Bölümlerin (Pompaların) Teknik Özelliklerini Kaydet ---
        if (submissionDTO.getFilledSections() != null) {
            for (SectionDTO sectionDTO : submissionDTO.getFilledSections()) {
                // Şablondaki asıl bölümü bul
                FormSection formSection = formSectionRepository.findById(sectionDTO.getSectionId())
                        .orElseThrow(() -> new RuntimeException("Bölüm bulunamadı: " + sectionDTO.getSectionId()));

                FilledSection filledSection = new FilledSection();
                filledSection.setFilledForm(savedForm);
                filledSection.setFormSection(formSection);
                filledSection.setTechnicalData(sectionDTO.getTechnicalData());
                filledSection.setNotes(sectionDTO.getNotes());

                filledSectionRepository.save(filledSection);
            }
        }

        // 3. Formun içindeki soruların cevaplarını tek tek kaydet (SENİN KODUN)
        if (submissionDTO.getAnswers() != null) {
            for (AnswerDTO answerDTO : submissionDTO.getAnswers()) {
                Question question = questionRepository.findById(answerDTO.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Soru bulunamadı: " + answerDTO.getQuestionId()));

                FormAnswer answer = new FormAnswer();
                answer.setFilledForm(savedForm);
                answer.setQuestion(question);
                answer.setAnswerValue(answerDTO.getAnswerValue());
                answer.setNotes(answerDTO.getNotes());

                formAnswerRepository.save(answer);
            }
        }

        // ELİT KÜRESEL LOG TETİKLENİYOR (SENİN KODUN)
        logService.log(
                currentUsername,
                "FORM_OLUŞTURMA",
                company.getName() + " firması için #" + savedForm.getId() + " nolu yeni yangın bakım formu başarıyla oluşturuldu."
        );

        return savedForm;
    }

    // Filtrelere göre formları ara
    public List<FilledForm> searchForms(Long companyId, LocalDate visitDate) {
        return filledFormRepository.filterForms(companyId, visitDate);
    }

    public FormDetailsDTO getFormDetails(Long id) {
        FilledForm filledForm = filledFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form bulunamadı: " + id));
        List<FormAnswer> answers = formAnswerRepository.findByFilledFormId(id);

        // 1. DTO'yu constructor ile üret
        FormDetailsDTO dto = new FormDetailsDTO(filledForm, answers);

        // 2. Tablodaki gerçek audit alanlarını DTO'ya map et
        dto.setCreatedUser(filledForm.getCreatedUser());
        dto.setUpdateUser(filledForm.getUpdateUser());

        if (filledForm.getCreatedDate() != null) {
            dto.setCreatedDate(filledForm.getCreatedDate().toString());
        }

        // Eğer tablonuzdaki güncelleme tarihi field adı farklıysa (örn: getUpdateAt) ona göre revize edebilirsiniz
        if (filledForm.getUpdateDate() != null) {
            dto.setUpdateDate(filledForm.getUpdateDate().toString());
        }

        return dto;
    }

    @Transactional
    public FilledForm updateForm(Long id, FormSubmissionDTO updateDTO, String currentUsername) {
        if (id == null) throw new RuntimeException("Güncellenecek formun ID bilgisi boş olamaz!");

        // 1. Mevcut Ana Formu ve Tanımları Veritabanından Doğrula
        FilledForm existingForm = filledFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Düzenlenmek istenen form bulunamadı! ID: " + id));

        Company company = companyRepository.findById(updateDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Seçilen şirket bulunamadı!"));

        FormTemplate template = formTemplateRepository.findById(updateDTO.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Seçilen form şablonu bulunamadı!"));

        // 2. Ana Form Üst Bilgilerini Güncelle
        existingForm.setCompany(company);
        existingForm.setTemplate(template);
        existingForm.setTechnicianName(updateDTO.getTechnicianName());
        existingForm.setGeneralNotes(updateDTO.getGeneralNotes());
        existingForm.setCompanyOfficialName(updateDTO.getCompanyOfficialName());

        if (updateDTO.getVisitDate() != null && !updateDTO.getVisitDate().isEmpty()) {
            existingForm.setVisitDate(java.time.LocalDate.parse(updateDTO.getVisitDate()));
        }
        if (updateDTO.getTechnicianSignatureUrl() != null) {
            existingForm.setTechnicianSignatureUrl(updateDTO.getTechnicianSignatureUrl());
        }
        if (updateDTO.getCompanyOfficialSignatureUrl() != null) {
            existingForm.setCompanyOfficialSignatureUrl(updateDTO.getCompanyOfficialSignatureUrl());
        }

        // 3. Eski Alt Verileri Doğrudan Repository Üzerinden Sil (Veritabanı Temizliği)
        List<FilledSection> oldSections = filledSectionRepository.findByFilledFormId(id);
        if (oldSections != null && !oldSections.isEmpty()) {
            filledSectionRepository.deleteAll(oldSections);
        }

        List<FormAnswer> oldAnswers = formAnswerRepository.findByFilledFormId(id);
        if (oldAnswers != null && !oldAnswers.isEmpty()) {
            formAnswerRepository.deleteAll(oldAnswers);
        }

        // 4. Yeni Bölüm (Pompa) Teknik Verilerini Döngüyle Yeniden Kaydet (Senin submitForm mantığınla)
        if (updateDTO.getFilledSections() != null) {
            for (SectionDTO sectionDTO : updateDTO.getFilledSections()) {
                if (sectionDTO.getSectionId() == null) continue;

                FormSection formSection = formSectionRepository.findById(sectionDTO.getSectionId())
                        .orElseThrow(() -> new RuntimeException("Bölüm bulunamadı: " + sectionDTO.getSectionId()));

                FilledSection filledSection = new FilledSection();
                filledSection.setFilledForm(existingForm);
                filledSection.setFormSection(formSection);
                filledSection.setTechnicalData(sectionDTO.getTechnicalData());
                filledSection.setNotes(sectionDTO.getNotes());

                filledSectionRepository.save(filledSection);
            }
        }

        // 5. Yeni Soru Cevaplarını Döngüyle Yeniden Kaydet (Senin submitForm mantığınla)
        if (updateDTO.getAnswers() != null) {
            for (AnswerDTO answerDTO : updateDTO.getAnswers()) {
                if (answerDTO.getQuestionId() == null) continue;

                Question question = questionRepository.findById(answerDTO.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Soru bulunamadı: " + answerDTO.getQuestionId()));

                FormAnswer answer = new FormAnswer();
                answer.setFilledForm(existingForm);
                answer.setQuestion(question);
                answer.setAnswerValue(answerDTO.getAnswerValue());
                answer.setNotes(answerDTO.getNotes());

                formAnswerRepository.save(answer);
            }
        }

        // 6. Ana Formu Kaydet ve Elit Sistem Logunu Tetikle
        FilledForm savedForm = filledFormRepository.save(existingForm);

        logService.log(
                currentUsername,
                "FORM_GÜNCELLEME",
                company.getName() + " firmasına ait #" + savedForm.getId() + " nolu yangın bakım formu başarıyla güncellendi."
        );

        return savedForm;
    }

    public List<Question> getQuestionsByTemplateId(Long templateId, String status) {

        // Yeni Question modelinde 'status' alanı bulunmadığı için doğrudan şablona (bölümler üzerinden) ait tüm soruları çekiyoruz.
        return questionRepository.findBySectionTemplateIdOrderByDisplayOrderAsc(templateId);
    }

    public Page<FilledForm> getPaginatedAndFilteredForms(int page, int size, String search) {
        // Formları her zaman ID'sine göre azalan (en son eklenen en üstte) şekilde sırala
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return formRepository.findBySearchTerm(search, pageable);
    }

}