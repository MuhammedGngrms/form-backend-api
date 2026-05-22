package com.form.controller;

import com.form.model.Company;
import com.form.repository.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // 1. Tüm Şirketleri Listele
    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies(@RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(companyRepository.findByStatus(status));
        }
        return ResponseEntity.ok(companyRepository.findAll());
    }

    // 2. Yeni Şirket Ekle
    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        return ResponseEntity.ok(companyRepository.save(company));
    }

    // 3. Şirket Bilgisi Güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long id, @RequestBody Company companyDetails) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));

        company.setName(companyDetails.getName());
        company.setEmail(companyDetails.getEmail());
        company.setLogo(companyDetails.getLogo());
        if (companyDetails.getStatus() != null) {
            company.setStatus(companyDetails.getStatus()); // Durum güncellenebilsin
        }
        return ResponseEntity.ok(companyRepository.save(company));
    }

    // 3. Şirketi Pasife Al (Soft DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Company> deleteCompany(@PathVariable Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));

        company.setStatus("D"); // Veriyi silmiyoruz, durumunu 'D' (Pasif) yapıyoruz
        return ResponseEntity.ok(companyRepository.save(company));
    }
}