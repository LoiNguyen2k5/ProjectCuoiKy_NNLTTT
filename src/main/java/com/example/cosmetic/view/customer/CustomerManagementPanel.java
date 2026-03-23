package com.example.cosmetic.view.customer;

import javax.swing.*;
import java.awt.*;

public class CustomerManagementPanel extends JPanel {
    // Khai báo các thành phần giao diện (nếu bạn cần dùng ở Controller thì để public)
    public JButton btnAdd, btnEdit, btnDelete;
    public JTable table;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Giao diện tạm thời để bạn chạy thử
        add(new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER), BorderLayout.NORTH);
        
        // Thêm bảng hoặc các nút bấm ở đây
        JPanel pnlAction = new JPanel();
        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        pnlAction.add(btnAdd);
        pnlAction.add(btnEdit);
        pnlAction.add(btnDelete);
        add(pnlAction, BorderLayout.SOUTH);
    }
}