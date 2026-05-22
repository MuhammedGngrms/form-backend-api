package com.form.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class FormSubmissionDTO {
    private Long companyId;
    private Long templateId;
    private String technicianName;
    private String generalNotes;
    private String technicianSignatureUrl;
    private String companyOfficialSignatureUrl;
    private String visitDate; // String kalmalı, servis içinde parse ediyoruz
    private String companyOfficialName;

    // Yeni Dinamik Listelerimiz
    private List<SectionDTO> filledSections;
    private List<AnswerDTO> answers;

    // BOŞ CONSTRUCTOR
    public FormSubmissionDTO() {}
}