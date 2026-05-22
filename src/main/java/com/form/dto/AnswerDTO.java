package com.form.dto;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long questionId;
    private String answerValue; // Örn: "EVET", "HAYIR" veya text
    private String notes; // Varsa eklenen notlar

    public AnswerDTO() {}
}