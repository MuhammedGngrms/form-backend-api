package com.form.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "template_questions")
public class TemplateQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private FormTemplate template;

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @Column(name = "input_type", nullable = false)
    private String inputType; // 'YES_NO', 'TEXT', 'MULTIPLE_CHOICE'

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "options", columnDefinition = "jsonb")
    private String options;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "status", length = 1)
    private String status = "S"; // S: Aktif, D: Pasif
}