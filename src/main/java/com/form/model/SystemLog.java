package com.form.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "system_logs")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String action; // Örn: "ŞİFRE_DEĞİŞTİRME", "PERSONEL_ENGELLEME", "YENİ_FORM_KAYDI"

    @Column(length = 500)
    private String description; // Örn: "ahmet_seba isimli teknisyenin sisteme erişimi engellendi."

    private LocalDateTime timestamp;

    public SystemLog() {}

    public SystemLog(String username, String action, String description) {
        this.username = username;
        this.action = action;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

}