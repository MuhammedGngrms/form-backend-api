package com.form.controller;

import com.form.model.SystemLog;
import com.form.service.SystemLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {

    private final SystemLogService logService;

    public AdminLogController(SystemLogService logService) {
        this.logService = logService; // Atama hatası burada milimetrik olarak çözüldü
    }

    @GetMapping
    public ResponseEntity<Page<SystemLog>> getSystemLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "HEPSİ") String action) {

        return ResponseEntity.ok(logService.getLogsWithPagination(action, page, size));
    }
}