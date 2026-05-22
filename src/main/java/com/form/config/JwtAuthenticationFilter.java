package com.form.config;

import com.form.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // Constructor Injection
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. İstekte gelen "Authorization" başlığını oku
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // 2. Başlık dolu mu ve standart "Bearer " önekiyle mi başlıyor kontrol et
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7); // "Bearer " yazısını sıyır, sadece saf şifreli token'ı al
            try {
                username = jwtUtil.extractUsername(jwtToken); // Token içinden kullanıcı adını cımbızla
            } catch (Exception e) {
                logger.error("JWT Token ayrıştırılırken hata oluştu: " + e.getMessage());
            }
        }

        // 3. Kullanıcı adı bulunduysa ve Spring Security hafızasında (Context) bu istek daha önce doğrulanmadıysa işle
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Veritabanımızdan kullanıcının güncel şifre/rol detaylarını yükle
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Token'ın süresi bitmiş mi veya sahte mi kontrol et
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                
                // Token geçerliyse, Spring Security'nin anlayacağı resmi bir "Giriş Kartı" (Authentication) oluştur
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Resmi giriş kartını sistemin güvenli hafızasına yerleştir (Artık kullanıcı onaylandı)
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // 5. İstediğin gümrük kontrolü bitti, isteğin yoluna (Controller'a) devam etmesine izin ver
        filterChain.doFilter(request, response);
    }
}