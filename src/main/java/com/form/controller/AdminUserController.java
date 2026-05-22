package com.form.controller;

import com.form.model.User;
import com.form.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final CustomUserDetailsService userDetailsService;

    public AdminUserController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // 1. Tüm personelleri getir
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userDetailsService.getAllUsers());
    }

    // 2. Yeni personel kaydet (Hatalı kısım burasıydı, temizlendi)
    @PostMapping
    public ResponseEntity<?> createNewUser(@RequestBody User user) {
        try {
            User createdUser = userDetailsService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Kullanıcıyı Aktif/Pasif yap
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, Principal principal) {
        try {
            // İstek atan adminin kullanıcı adını token'dan çekiyoruz
            String adminUsername = principal != null ? principal.getName() : "SYSTEM";

            User updatedUser = userDetailsService.toggleUserStatus(id, adminUsername);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}