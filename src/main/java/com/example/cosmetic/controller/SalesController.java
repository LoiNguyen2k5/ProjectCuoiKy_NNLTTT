package com.example.cosmetic.controller;

import com.example.cosmetic.model.entity.*;
import com.example.cosmetic.service.*;
import com.example.cosmetic.view.invoice.SalesPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SalesController {
    private final SalesPanel view;
    private final ProductService productService;
    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private final Staff currentStaff;

    public SalesController(SalesPanel view, ProductService ps, CustomerService cs, InvoiceService is, Staff staff) {
        this.view = view;
        this.productService = ps;
        this.customerService = cs;
        this.invoiceService = is;
        this.currentStaff = staff;

        loadInitialData();
        initEvents();
    }

    private void loadInitialData() {
        // Xóa dữ liệu cũ tránh trùng lặp nếu mở lại panel
        view.getCbProduct().removeAllItems();
        view.getCbCustomer().removeAllItems();

        // Load sản phẩm
        List<Product> products = productService.getAllProducts();
        if (products != null) {
            products.forEach(p -> view.getCbProduct().addItem(p));
        }

        // Load khách hàng (Sẽ hiển thị Tên - SĐT nhờ hàm toString() trong Entity Customer)
        List<Customer> customers = customerService.getAllCustomers();
        if (customers != null) {
            customers.forEach(c -> view.getCbCustomer().addItem(c));
        }
    }

    private void initEvents() {
        // Sự kiện khi chọn Khách hàng: Tự động điền tên vào ô text bên cạnh
        view.getCbCustomer().addActionListener(e -> {
            Customer selected = (Customer) view.getCbCustomer().getSelectedItem();
            if (selected != null) {
                view.getTxtCustomerName().setText(selected.getName());
            }
        });

        // Sự kiện 1: Thêm sản phẩm vào giỏ hàng
        view.getBtnAddCart().addActionListener(e -> addToCart());

        // Sự kiện 2: Thanh toán hóa đơn
        view.getBtnCheckout().addActionListener(e -> processCheckout());
    }

    private void addToCart() {
        Product selectedProd = (Product) view.getCbProduct().getSelectedItem();
        if (selectedProd == null) return;

        int qty;
        try {
            qty = Integer.parseInt(view.getTxtQuantity().getText());
            if (qty <= 0) throw new Exception();
            
            // Kiểm tra sơ bộ số lượng tồn kho ngay tại View
            if (qty > selectedProd.getQuantity()) {
                JOptionPane.showMessageDialog(view, "Sản phẩm [" + selectedProd.getName() + "] không đủ hàng (Còn: " + selectedProd.getQuantity() + ")");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Số lượng phải là số nguyên dương!");
            return;
        }

        DefaultTableModel model = view.getCartModel();
        boolean isExist = false;
        
        // Kiểm tra nếu sản phẩm đã có trong giỏ thì cộng dồn số lượng
        for (int i = 0; i < model.getRowCount(); i++) {
            Long idInTable = (Long) model.getValueAt(i, 0);
            if (idInTable.equals(selectedProd.getId())) {
                int currentQty = (int) model.getValueAt(i, 3);
                int newQty = currentQty + qty;
                
                // Kiểm tra lại tồn kho khi cộng dồn
                if (newQty > selectedProd.getQuantity()) {
                    JOptionPane.showMessageDialog(view, "Tổng số lượng trong giỏ vượt quá tồn kho!");
                    return;
                }
                
                model.setValueAt(newQty, i, 3);
                BigDecimal newSubTotal = selectedProd.getPrice().multiply(new BigDecimal(newQty));
                model.setValueAt(newSubTotal, i, 5);
                isExist = true;
                break;
            }
        }

        // Nếu chưa có thì thêm dòng mới
        if (!isExist) {
            BigDecimal subTotal = selectedProd.getPrice().multiply(new BigDecimal(qty));
            model.addRow(new Object[]{
                selectedProd.getId(),
                selectedProd.getBarcode(),
                selectedProd.getName(),
                qty,
                selectedProd.getPrice(),
                subTotal
            });
        }
        
        calculateGrandTotal();
    }

    private void calculateGrandTotal() {
        BigDecimal total = BigDecimal.ZERO;
        DefaultTableModel model = view.getCartModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            total = total.add((BigDecimal) model.getValueAt(i, 5));
        }
        view.getTxtTotal().setText(total.toString());
    }

    private void processCheckout() {
        DefaultTableModel model = view.getCartModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng đang trống, không thể thanh toán!");
            return;
        }

        Customer selectedCust = (Customer) view.getCbCustomer().getSelectedItem();
        if (selectedCust == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Xác nhận thanh toán hóa đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // 1. Khởi tạo đối tượng Invoice
            Invoice invoice = new Invoice();
            invoice.setStaff(currentStaff);
            invoice.setCustomer(selectedCust);
            invoice.setTotalAmount(new BigDecimal(view.getTxtTotal().getText()));

            // 2. Đóng gói danh sách InvoiceDetail
            List<InvoiceDetail> details = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                InvoiceDetail detail = new InvoiceDetail();
                
                // Lấy sản phẩm từ ID
                Long productId = (Long) model.getValueAt(i, 0);
                Product p = new Product(); 
                p.setId(productId); // Chỉ cần set ID để Hibernate map khóa ngoại
                
                detail.setProduct(p);
                detail.setQuantity((int) model.getValueAt(i, 3));
                detail.setPrice((BigDecimal) model.getValueAt(i, 4));
                detail.setInvoice(invoice);
                details.add(detail);
            }
            invoice.setDetails(details);

            // 3. Gọi Service thực hiện lưu DB và trừ kho (Transaction)
            invoiceService.processCheckout(invoice);
            
            JOptionPane.showMessageDialog(view, "Thanh toán thành công! Hóa đơn đã được lưu và trừ kho.");
            
            // 4. Làm mới giỏ hàng
            model.setRowCount(0);
            view.getTxtTotal().setText("0");
            view.getTxtCustomerName().setText("");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi xử lý thanh toán: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}