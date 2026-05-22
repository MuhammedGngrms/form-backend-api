package com.form.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaConfig {
    // Bu boş konfigürasyon sınıfı, JPA'nın arka planda tarih ve kullanıcı takibini başlatmasını sağlar.
}