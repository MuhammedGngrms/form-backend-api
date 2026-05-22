package com.form.controller;

import com.form.config.JwtUtil;
import com.form.model.AuthRequest;
import com.form.model.AuthResponse;
import com.form.model.PasswordChangeRequest;
import com.form.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    // Constructor Injection
    public AuthController(AuthenticationManager authenticationManager, 
                          CustomUserDetailsService userDetailsService, 
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // GİRİŞ YAPMA API UÇ NOKTASI (POST /api/auth/login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            // Hatanın asıl sebebini Java konsoluna (System.out) yazdırıyoruz:
            System.out.println("GİRİŞ HATASI DETAYI: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(401).body("Kullanıcı adı veya şifre hatalı! Detay: " + e.getMessage());
        }
        // 2. Kimlik doğrulama başarılıysa kullanıcının bilgilerini yükle
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // 3. Bu kullanıcı için özel dijital anahtarı (JWT) üret
        final String jwtToken = jwtUtil.generateToken(userDetails);

        // Kullanıcının rollerini string setine çevir (React'ın kolay okuması için)
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 4. Token, Kullanıcı Adı ve Rol bilgilerini şık bir paket halinde React'a geri dön
        return ResponseEntity.ok(new AuthResponse(jwtToken, userDetails.getUsername(), roles));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest, Principal principal) {
        try {
            // Principal nesnesi bize o an JWT token ile giriş yapmış olan aktif kullanıcının adını verir
            String currentUsername = principal.getName();

            // Şifre değiştirme işini servise devret
            userDetailsService.changePassword(currentUsername, passwordChangeRequest);

            return ResponseEntity.ok("Şifreniz başarıyla güncellendi.");
        } catch (Exception e) {
            // Hata durumunda (Örn: Eski şifre yanlışsa) React'a 400 Bad Request fırlat
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}