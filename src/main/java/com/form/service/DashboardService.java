package com.form.service;

import com.form.dto.DashboardStatsDTO;
import com.form.model.FilledForm;
import com.form.repository.CompanyRepository;
import com.form.repository.FilledFormRepository;
import com.form.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FilledFormRepository formRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public DashboardService(FilledFormRepository formRepository, CompanyRepository companyRepository, UserRepository userRepository) {
        this.formRepository = formRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // 1. Temel Sayılar
        stats.setTotalForms(formRepository.count());
        stats.setTotalCompanies(companyRepository.count());
        stats.setTotalUsers(userRepository.count());

        // Tüm formları çekiyoruz
        List<FilledForm> allForms = formRepository.findAll();

        // 2. Grafik İçin Aylık Trend Hesaplama (Mevcut kodunuz)
        Map<String, Long> trendMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("tr"));
        for (int i = 5; i >= 0; i--) {
            trendMap.put(LocalDate.now().minusMonths(i).format(formatter), 0L);
        }
        for (FilledForm form : allForms) {
            LocalDate date = form.getVisitDate() != null ? form.getVisitDate() :
                    (form.getCreatedDate() != null ? form.getCreatedDate().toLocalDate() : LocalDate.now());
            String label = date.format(formatter);
            if (trendMap.containsKey(label)) {
                trendMap.put(label, trendMap.get(label) + 1);
            }
        }
        List<Map<String, Object>> trendList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : trendMap.entrySet()) {
            Map<String, Object> point = new HashMap<>();
            point.put("name", entry.getKey());
            point.put("formSayisi", entry.getValue());
            trendList.add(point);
        }
        stats.setMonthlyTrends(trendList);

        // 3. YENI EKLENEN: Teknisyenlerin Form Sayılarını Hesaplama
        // Formları teknisyen adına göre gruplayıp sayısını buluyoruz
        Map<String, Long> techCounts = allForms.stream()
                .filter(f -> f.getTechnicianName() != null)
                .collect(Collectors.groupingBy(FilledForm::getTechnicianName, Collectors.counting()));

        List<Map<String, Object>> techList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : techCounts.entrySet()) {
            Map<String, Object> techData = new HashMap<>();
            techData.put("username", entry.getKey());
            techData.put("count", entry.getValue());
            techList.add(techData);
        }
        // En çok form dolduran personel en üstte gözüksün diye sıralıyoruz
        techList.sort((m1, m2) -> Long.compare((Long) m2.get("count"), (Long) m1.get("count")));

        stats.setTechnicianFormCounts(techList);

        return stats;
    }
}