package com.example.cosmetic.service.impl;

import com.example.cosmetic.service.StatisticsService;
import com.example.cosmetic.repository.impl.StatisticsRepositoryImpl;

public class StatisticsServiceImpl implements StatisticsService {
    private StatisticsRepositoryImpl repo;

    // ĐÂY LÀ CONSTRUCTOR MÀ VS CODE ĐANG ĐÒI
    public StatisticsServiceImpl(StatisticsRepositoryImpl repo) {
        this.repo = repo;
    }

    @Override
    public void calculateStatistics() {
        // Gọi repo để lấy dữ liệu và tính toán
        double revenue = repo.getTotalRevenue();
        System.out.println("Doanh thu: " + revenue);
    }
}