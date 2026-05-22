package com.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "filled_sections")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FilledSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu cihaz bilgisi hangi doldurulan forma ait?
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filled_form_id")
    @JsonIgnore
    private FilledForm filledForm;

    // Bu cihaz hangi şablon bölümünün karşılığı? (Örn: Jokey Pompa şablonu)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form_section_id")
    private FormSection formSection;

    // Cihazın teknik bilgileri (Bunu JSON olarak tutmak inanılmaz esneklik sağlar. 
    // İleride "Pompa Basıncı" yerine "Köpük Oranı" sorulursa veritabanını bozmadan JSON içine eklersin)
    @Column(columnDefinition = "TEXT")
    private String technicalData; // Örn: {"model": "A1", "seriNo": "12345", "basinc": "10"}

    @Column(columnDefinition = "TEXT")
    private String notes;

}