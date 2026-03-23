package com.example.cosmetic.repository;

import java.util.Map;

public interface StatisticsRepository {
    // Ví dụ: Lấy doanh thu theo tháng (Trả về Map với Key là tên tháng, Value là số tiền)
    Map<String, Double> getMonthlyRevenue();
    
    // Ví dụ: Lấy tổng số sản phẩm đã bán
    int getTotalProductsSold();
}