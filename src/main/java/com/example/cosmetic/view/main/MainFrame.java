package com.example.cosmetic.view.main;

import com.example.cosmetic.model.entity.Staff;
import com.example.cosmetic.model.enums.StaffRole;
import com.example.cosmetic.repository.impl.*;
import com.example.cosmetic.service.impl.*;
import com.example.cosmetic.view.category.CategoryManagementPanel;
import com.example.cosmetic.view.brand.BrandManagementPanel;
import com.example.cosmetic.view.supplier.SupplierManagementPanel;
import com.example.cosmetic.view.product.ProductManagementPanel;
import com.example.cosmetic.view.customer.CustomerManagementPanel;
import com.example.cosmetic.view.dashboard.DashboardPanel;
import com.example.cosmetic.view.statistics.StatisticsPanel;
import com.example.cosmetic.view.invoice.SalesPanel;
import com.example.cosmetic.controller.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Staff currentStaff;
    private JPanel centerPanel;

    public MainFrame(Staff currentStaff) {
        this.currentStaff = currentStaff;
        
        setTitle("Hệ thống Quản lý Mỹ phẩm - " + currentStaff.getFullName());
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        centerPanel = new JPanel(new BorderLayout());
        openDashboard(); // Mặc định khi vào là hiện Dashboard

        add(centerPanel, BorderLayout.CENTER);
        setupMenuBasedOnRole();
    }

    private void setupMenuBasedOnRole() {
        JMenuBar menuBar = new JMenuBar();
        
        // --- Menu Hệ Thống ---
        JMenu menuSystem = new JMenu("Hệ Thống");
        JMenuItem itemDashboard = new JMenuItem("Bảng điều khiển");
        JMenuItem itemLogout = new JMenuItem("Đăng xuất");
        menuSystem.add(itemDashboard);
        menuSystem.add(itemLogout);

        // --- Menu Bán Hàng ---
        JMenu menuSales = new JMenu("Bán Hàng");
        JMenuItem itemSales = new JMenuItem("Lập hóa đơn");
        menuSales.add(itemSales);

        // --- Menu Danh Mục ---
        JMenu menuCatalog = new JMenu("Danh Mục");
        JMenuItem itemCategory = new JMenuItem("Loại Mỹ Phẩm");
        JMenuItem itemBrand = new JMenuItem("Thương Hiệu");
        JMenuItem itemSupplier = new JMenuItem("Nhà Cung Cấp");
        JMenuItem itemCustomer = new JMenuItem("Khách Hàng");
        JMenuItem itemProduct = new JMenuItem("Sản Phẩm");
        
        menuCatalog.add(itemCategory);
        menuCatalog.add(itemBrand);
        menuCatalog.add(itemSupplier);
        menuCatalog.add(itemCustomer);
        menuCatalog.addSeparator();
        menuCatalog.add(itemProduct);

        menuBar.add(menuSystem);
        menuBar.add(menuSales);
        menuBar.add(menuCatalog);

        // --- Menu Thống Kê (Chỉ Admin) ---
        if (currentStaff.getRole() == StaffRole.ADMIN) {
            JMenu menuStats = new JMenu("Thống Kê");
            JMenuItem itemGeneralStats = new JMenuItem("Báo cáo doanh thu");
            menuStats.add(itemGeneralStats);
            menuBar.add(menuStats);
            
            itemGeneralStats.addActionListener(e -> openStatistics());
        }

        setJMenuBar(menuBar);

        // Gắn sự kiện
        itemDashboard.addActionListener(e -> openDashboard());
        itemSales.addActionListener(e -> openSales());
        itemCategory.addActionListener(e -> openCategoryManagement());
        itemBrand.addActionListener(e -> openBrandManagement());
        itemSupplier.addActionListener(e -> openSupplierManagement());
        itemCustomer.addActionListener(e -> openCustomerManagement());
        itemProduct.addActionListener(e -> openProductManagement());
        itemLogout.addActionListener(e -> {
            this.dispose();
            // Gọi lại màn hình Login nếu cần
        });
    }

    private void switchPanel(JPanel newPanel) {
        centerPanel.removeAll();
        centerPanel.add(newPanel, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void openDashboard() {
        StatisticsRepositoryImpl repo = new StatisticsRepositoryImpl();
        StatisticsServiceImpl service = new StatisticsServiceImpl(repo);
        DashboardPanel view = new DashboardPanel();
        // Giả sử có DashboardController, nếu không thì chỉ hiện view
        switchPanel(view);
    }

    private void openSales() {
        try {
            ProductRepositoryImpl pRepo = new ProductRepositoryImpl();
            CustomerRepositoryImpl cRepo = new CustomerRepositoryImpl();
            InvoiceRepositoryImpl iRepo = new InvoiceRepositoryImpl();
            ProductServiceImpl pService = new ProductServiceImpl(pRepo);
            CustomerServiceImpl cService = new CustomerServiceImpl(cRepo);
            InvoiceServiceImpl iService = new InvoiceServiceImpl(iRepo);

            SalesPanel view = new SalesPanel();
            new SalesController(view, pService, cService, iService, currentStaff);
            switchPanel(view);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi load Bán hàng: " + e.getMessage());
        }
    }

    private void openCategoryManagement() {
        CategoryRepositoryImpl repo = new CategoryRepositoryImpl();
        CategoryServiceImpl service = new CategoryServiceImpl(repo);
        CategoryManagementPanel view = new CategoryManagementPanel();
        new CategoryController(service, view, currentStaff);
        switchPanel(view);
    }

    private void openBrandManagement() {
        BrandRepositoryImpl repo = new BrandRepositoryImpl();
        BrandServiceImpl service = new BrandServiceImpl(repo);
        BrandManagementPanel view = new BrandManagementPanel();
        new BrandController(service, view, currentStaff);
        switchPanel(view);
    }

    private void openSupplierManagement() {
        SupplierRepositoryImpl repo = new SupplierRepositoryImpl();
        SupplierServiceImpl service = new SupplierServiceImpl(repo);
        SupplierManagementPanel view = new SupplierManagementPanel();
        new SupplierController(service, view, currentStaff);
        switchPanel(view);
    }

    private void openCustomerManagement() {
        CustomerRepositoryImpl repo = new CustomerRepositoryImpl();
        CustomerServiceImpl service = new CustomerServiceImpl(repo);
        CustomerManagementPanel view = new CustomerManagementPanel();
        new CustomerController(service, view, currentStaff);
        switchPanel(view);
    }

    private void openProductManagement() {
        ProductRepositoryImpl pRepo = new ProductRepositoryImpl();
        CategoryRepositoryImpl cRepo = new CategoryRepositoryImpl();
        BrandRepositoryImpl bRepo = new BrandRepositoryImpl();
        ProductServiceImpl pService = new ProductServiceImpl(pRepo);
        CategoryServiceImpl cService = new CategoryServiceImpl(cRepo);
        BrandServiceImpl bService = new BrandServiceImpl(bRepo);
        ProductManagementPanel view = new ProductManagementPanel();
        new ProductController(pService, cService, bService, view, currentStaff);
        switchPanel(view);
    }

    private void openStatistics() {
        StatisticsRepositoryImpl repo = new StatisticsRepositoryImpl();
        StatisticsServiceImpl service = new StatisticsServiceImpl(repo);
        StatisticsPanel view = new StatisticsPanel();
        new StatisticsController(service, view);
        switchPanel(view);
    }
}