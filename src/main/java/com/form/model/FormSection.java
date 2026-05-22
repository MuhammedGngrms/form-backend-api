package com.form.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@Entity
@Table(name = "form_sections")
public class FormSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Örn: "Jokey Pompa", "I. Dizel Pompa"
    
    private int displayOrder; // Ekranda ve PDF'te hangi sırayla çıkacağı (1, 2, 3...)

    // Bu bölüm hangi ana şablona ait?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    @JsonIgnore // Sonsuz döngüyü engellemek için
    private FormTemplate template;

    // Bu pompanın / bölümün kendine ait soruları
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

}