package com.form.dto;

import com.form.model.FilledForm;
import com.form.model.FormAnswer;
import lombok.Data;
import java.util.List;

@Data
public class FormDetailsDTO {
    private FilledForm filledForm;
    private List<FormAnswer> answers;
    private String createdUser;
    private String createdDate;
    private String updateUser;
    private String updateDate;

    public FormDetailsDTO(FilledForm filledForm, List<FormAnswer> answers) {
        this.filledForm = filledForm;
        this.answers = answers;
    }
}