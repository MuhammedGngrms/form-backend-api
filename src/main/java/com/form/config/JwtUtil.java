package com.form.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // Token geçerlilik süresi (Örn: 10 Saat = 10 * 60 * 60 * 1000 milisaniye)
    private final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000;

    // Şifreleme anahtarını hazırlayan yardımcı metod
    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. ADIM: Başarılı Giriş Yapan Kullanıcıya Token Üretme (Generate Token)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Kullanıcının rollerini token içerisine "roles" ismiyle gömüyoruz (React okuyabilsin diye)
        claims.put("roles", userDetails.getAuthorities());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. ADIM: Gelen Token İçinden Kullanıcı Adını Okuma
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. ADIM: Gelen Token'ın Süresinin Bitip Bitmediğini Kontrol Etme
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Token içinden veri cımbızlamaya yarayan genel metod
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 4. ADIM: Token'ın Doğruluğunu Teyit Etme (Validate Token)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}