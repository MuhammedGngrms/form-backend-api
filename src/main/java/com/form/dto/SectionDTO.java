package com.form.dto;

public class SectionDTO {
    
    private Long sectionId;
    private String technicalData;
    private String notes;

    // GETTER VE SETTER METOTLARI
    public Long getSectionId() { 
        return sectionId; 
    }
    
    public void setSectionId(Long sectionId) { 
        this.sectionId = sectionId; 
    }

    public String getTechnicalData() { 
        return technicalData; 
    }
    
    public void setTechnicalData(String technicalData) { 
        this.technicalData = technicalData; 
    }

    public String getNotes() { 
        return notes; 
    }
    
    public void setNotes(String notes) { 
        this.notes = notes; 
    }
}