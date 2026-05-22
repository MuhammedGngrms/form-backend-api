package com.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "companies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    @Column(columnDefinition = "TEXT") // Logo Base64 (uzun metin) olarak tutulacak
    private String logo;

    @Column(name = "status", length = 1)
    private String status = "S"; // S: Aktif, D: Pasif

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}