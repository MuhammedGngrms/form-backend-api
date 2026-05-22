package com.form.service;

import com.form.model.SystemLog;
import com.form.repository.SystemLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SystemLogService {

    private final SystemLogRepository logRepository;

    public SystemLogService(SystemLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // Kritik operasyonel işlemler hata alıp geri alınsa (rollback) bile logun veritabanına yazılması için REQUIRES_NEW kullanıyoruz
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String username, String action, String description) {
        SystemLog sysLog = new SystemLog(username, action, description);
        logRepository.save(sysLog);
    }

    public Page<SystemLog> getLogsWithPagination(String action, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Eğer aksiyon 'HEPSİ' ise veya boş gönderilmişse hepsini getir
        if (action == null || action.trim().isEmpty() || action.equals("HEPSİ")) {
            return logRepository.findAllByOrderByIdDesc(pageable);
        }

        // Sadece belirli bir aksiyon istenmişse filtreli getir
        return logRepository.findByActionOrderByIdDesc(action, pageable);
    }
}