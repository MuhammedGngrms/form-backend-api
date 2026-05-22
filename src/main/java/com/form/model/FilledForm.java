package com.form.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "filled_forms")
public class FilledForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi Müşteri/Firma için dolduruldu?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    // Hangi Şablon kullanılarak dolduruldu?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private FormTemplate template;

    private String technicianName;

    @Column(columnDefinition = "TEXT")
    private String generalNotes;

    @Column(columnDefinition = "TEXT")
    private String technicianSignatureUrl;

    @Column(columnDefinition = "TEXT")
    private String companyOfficialSignatureUrl;

    private LocalDateTime createdDate = LocalDateTime.now();

    // Bu formun içindeki "Doldurulmuş Pompalar/Bölümler"
    @OneToMany(mappedBy = "filledForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilledSection> filledSections = new ArrayList<>();

    // Bu formdaki tüm sorulara verilen cevaplar
    @OneToMany(mappedBy = "filledForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    private java.time.LocalDate visitDate;

    private String companyOfficialName;

    private String createdUser;
    private String updateUser;
    private String updateDate;

}