package com.example.cosmetic.view.main;

import com.example.cosmetic.model.entity.Staff;
import com.example.cosmetic.model.enums.StaffRole;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Staff currentStaff;

    public MainFrame(Staff currentStaff) {
        this.currentStaff = currentStaff;
        
        setTitle("Hệ thống Quản lý Cửa hàng Mỹ phẩm - " + currentStaff.getFullName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblWelcome = new JLabel("CHÀO MỪNG BẠN ĐẾN VỚI HỆ THỐNG (" + currentStaff.getRole() + ")", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblWelcome, BorderLayout.CENTER);

        // Nơi xử lý phân quyền UI
        setupMenuBasedOnRole();
    }

    private void setupMenuBasedOnRole() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuSales = new JMenu("Bán Hàng");
        JMenu menuCatalog = new JMenu("Danh Mục");
        JMenu menuStats = new JMenu("Thống Kê");

        menuBar.add(menuSales);
        menuBar.add(menuCatalog);
        menuBar.add(menuStats);

        // Nếu là nhân viên thường, ẩn tab Thống Kê đi
        if (currentStaff.getRole() == StaffRole.STAFF) {
            menuStats.setVisible(false);
        }

        setJMenuBar(menuBar);
    }
}