package com.example.cosmetic.controller;

import com.example.cosmetic.service.impl.StatisticsServiceImpl;
import com.example.cosmetic.view.StatisticsPanel;

public class StatisticsController {
    private StatisticsServiceImpl service;
    private StatisticsPanel view;

    // Constructor nhận vào service và view để điều khiển
    public StatisticsController(StatisticsServiceImpl service, StatisticsPanel view) {
        this.service = service;
        this.view = view;
        
        // Gọi hàm khởi tạo các sự kiện (nếu có)
        initEvent();
    }

    private void initEvent() {
        // Ví dụ: Bắt sự kiện khi nhấn nút trên StatisticsPanel
        // view.getBtnRefresh().addActionListener(e -> updateStatistics());
    }
}