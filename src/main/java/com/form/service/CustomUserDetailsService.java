package com.form.service;

import com.form.model.PasswordChangeRequest;
import com.form.model.Role;
import com.form.model.User;
import com.form.repository.UserRepository;
import com.form.repository.RoleRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SystemLogService logService;

    public CustomUserDetailsService(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    RoleRepository roleRepository,
                                    SystemLogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.logService = logService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User newUser) throws Exception {
        // Kullanıcı adı daha önce alınmış mı kontrol et
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            throw new Exception("Bu kullanıcı adı zaten sistemde kayıtlı!");
        }

        // Şifreyi bizim sistemle %100 uyumlu BCrypt motoruyla şifrele
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // Varsayılan olarak kullanıcısını "S" (Active) durumunda başlat
        newUser.setStatus("S");

        // Eğer kullanıcıya rol belirtilmemişse, varsayılan olarak ROLE_TECHNICIAN ata
        if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_TECHNICIAN")
                    .orElseThrow(() -> new Exception("ROLE_TECHNICIAN rolü sistemde bulunamadı!"));
            newUser.getRoles().add(defaultRole);
        }

        return userRepository.save(newUser);
    }

    // 3. KULLANICI DURUMUNU DEĞİŞTİR (Aktif / Pasif)
    public User toggleUserStatus(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Kullanıcı bulunamadı."));

        // "S" ise "A" (veya "P") yap, değilse "S" yap
        if ("S".equals(user.getStatus())) {
            user.setStatus("A"); // Pasif / Askıya alındı
        } else {
            user.setStatus("S"); // Aktif
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true) // Veritabanı bağlantısının (Session) kopmasını engeller
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Kullanıcıyı bul
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // 2. Rolleri oku ve boş kalmadığından emin ol
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    System.out.println("Yüklenen Rol Kontrolü (" + username + "): " + role.getName());
                    return new SimpleGrantedAuthority(role.getName());
                })
                .collect(Collectors.toList());

        // Eğer veritabanından roller bir şekilde boş geldiyse sisteme geçici rol ata (Çökmeyi önler)
        if (authorities.isEmpty()) {
            System.out.println("UYARI: " + username + " kullanıcısının rolleri veritabanında bulunamadı!");
        }

        // 3. Spring Security User nesnesini tam yetkiyle dön
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                "S".equals(user.getStatus()), // Etkin mi?
                true,
                true,
                true,
                authorities // Yetkiler buraya cuk oturmalı
        );
    }

    public void changePassword(String username, PasswordChangeRequest request) throws Exception {
        // 1. Kullanıcıyı bul
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Kullanıcı bulunamadı."));

        // 2. Girdiği eski şifre veritabanındaki (Bcrypt'li) şifreyle uyuşuyor mu kontrol et
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new Exception("Mevcut şifrenizi hatalı girdiniz!");
        }

        // 3. Yeni şifreyi sistemimizle %100 uyumlu şekilde BCrypt ile şifrele
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        // 4. Güncelle ve kaydet
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    public User toggleUserStatus(Long userId, String adminUsername) throws Exception {
        // Üç nokta hatası burada temizlendi, gerçek istisna fırlatılıyor
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Kullanıcı bulunamadı: " + userId));

        String oldStatus = user.getStatus();
        if ("S".equals(oldStatus)) {
            user.setStatus("A");
            logService.log(adminUsername, "PERSONEL_ENGELLEME",
                    user.getUsername() + " isimli teknisyenin sisteme erişimi admin tarafından askıya alındı.");
        } else {
            user.setStatus("S");
            logService.log(adminUsername, "PERSONEL_AKTİFLEŞTİRME",
                    user.getUsername() + " isimli teknisyenin erişim engeli kaldırıldı.");
        }
        return userRepository.save(user);
    }
}