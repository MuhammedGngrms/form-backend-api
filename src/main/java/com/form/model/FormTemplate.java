package com.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "form_templates")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FormTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String name;

    private String status = "S"; // S: Aktif, D: Pasif

    // Bir şablonun birden fazla bölümü (Örn: Jokey, Dizel 1) olabilir
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormSection> sections = new ArrayList<>();
}