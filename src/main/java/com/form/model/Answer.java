package com.form.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu cevap hangi forma ait?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filled_form_id")
    @JsonIgnore
    private FilledForm filledForm;

    // Bu cevap hangi soruya verildi?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private Question question;

    private String answerValue; // EVET, HAYIR
    
    @Column(columnDefinition = "TEXT")
    private String notes; // Varsa ek açıklama

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FilledForm getFilledForm() { return filledForm; }
    public void setFilledForm(FilledForm filledForm) { this.filledForm = filledForm; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getAnswerValue() { return answerValue; }
    public void setAnswerValue(String answerValue) { this.answerValue = answerValue; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}