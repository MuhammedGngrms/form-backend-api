package com.form.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardStatsDTO {
    private long totalForms;
    private long totalCompanies;
    private long totalUsers;
    private List<Map<String, Object>> monthlyTrends;
    private List<Map<String, Object>> technicianFormCounts;
}