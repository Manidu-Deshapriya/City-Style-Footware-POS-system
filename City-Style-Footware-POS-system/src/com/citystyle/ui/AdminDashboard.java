package com.citystyle.ui;

import com.citystyle.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * AdminDashboard extends BaseDashboard.
 * Adds User Management and Metadata (Categories/Brands) panels.
 */
public class AdminDashboard extends BaseDashboard {

    public AdminDashboard(User user) {
        super(user, "CityStyle Footwear Admin");

        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR);

        // Standard Panels
        contentPanel.add(createInventoryPanel(), "Inventory");
        contentPanel.add(createSalesPanel(), "Sales");
        contentPanel.add(createReturnsPanel(), "Returns");

        // Admin Specific Panels
        contentPanel.add(createUserManagementPanel(), "Users");
        contentPanel.add(createMetadataPanel(), "Meta");
        contentPanel.add(createDiscountRequestsPanel(), "DiscountReq");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "Inventory");
    }

    @Override
    protected JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 25, 20, 20));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel line1 = new JLabel("CITY STYLE");
        line1.setForeground(Color.WHITE);
        line1.setFont(new Font("Segoe UI", Font.BOLD, 28));
        line1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel line2 = new JLabel("FOOTWEAR");
        line2.setForeground(ACCENT_COLOR);
        line2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        line2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel line3 = new JLabel("ADMIN PANEL");
        line3.setForeground(new Color(231, 76, 60)); // Alizarin Red
        line3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        line3.setAlignmentX(Component.LEFT_ALIGNMENT);
        line3.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        logoPanel.add(line1);
        logoPanel.add(line2);
        logoPanel.add(line3);
        sidebar.add(logoPanel);

        sidebar.add(createSidebarButton("Inventory / Search", "Inventory"));
        sidebar.add(createSidebarButton("Point of Sale (POS)", "Sales"));
        sidebar.add(createSidebarButton("Returns & Approvals", "Returns"));

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(new JSeparator(JSeparator.HORIZONTAL));
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createSidebarButton("User Accounts", "Users"));
        sidebar.add(createSidebarButton("Categories & Brands", "Meta"));
        sidebar.add(createSidebarButton("Discount Requests", "DiscountReq"));

        sidebar.add(Box.createVerticalGlue());
        JButton logoutBtn = createSidebarButton("Logout", null);
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("User Accounts Management", JLabel.CENTER), BorderLayout.NORTH);

        String[] cols = { "ID", "Username", "Role" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtU = new JTextField(10);
        JTextField txtP = new JTextField(10);
        JComboBox<String> cbR = new JComboBox<>(new String[] { "Cashier", "Manager" });
        JButton btnAdd = new JButton("Create User");
        JButton btnDelete = new JButton("Delete User");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);

        bottom.add(new JLabel("User:"));
        bottom.add(txtU);
        bottom.add(new JLabel("Pass:"));
        bottom.add(txtP);
        bottom.add(cbR);
        bottom.add(btnAdd);
        bottom.add(btnDelete);
        panel.add(bottom, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            model.setRowCount(0);
            userDAO.getAllUsers()
                    .forEach(u -> model.addRow(new Object[] { u.getUserId(), u.getUsername(), u.getRole() }));
        };
        btnAdd.addActionListener(e -> {
            userDAO.createUser(txtU.getText(), txtP.getText(), (String) cbR.getSelectedItem());
            refresh.run();
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this,
                        "Delete this user? (Note: Cannot delete users with active sales records)", "Confirm",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try {
                        userDAO.deleteUser(id);
                        refresh.run();
                    } catch (java.sql.SQLException ex) {
                        String msg = "Could not delete user. They might have active sales/records.";
                        if (ex.getMessage().contains("foreign key")) {
                            msg = "Cannot delete this user because they have historical sales or return records.";
                        }
                        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });
        refresh.run();
        return panel;
    }

    private JPanel createMetadataPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 20));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createListPanel("Categories", metaDAO::getCategories, metaDAO::addCategory, metaDAO::updateCategory,
                metaDAO::deleteCategory));
        panel.add(createListPanel("Brands", metaDAO::getBrands, metaDAO::addBrand, metaDAO::updateBrand,
                metaDAO::deleteBrand));
        panel.add(createListPanel("Suppliers", metaDAO::getSuppliers, metaDAO::addSupplier, metaDAO::updateSupplier,
                metaDAO::deleteSupplier));
        return panel;
    }

    private JPanel createListPanel(String title, java.util.function.Supplier<List<String>> getter,
            java.util.function.Consumer<String> adder,
            java.util.function.BiConsumer<String, String> updater,
            java.util.function.Consumer<String> deleter) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        p.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel addP = new JPanel(new BorderLayout());
        JTextField txt = new JTextField();
        JButton btnAdd = new JButton("Add");
        addP.add(txt, BorderLayout.CENTER);
        addP.add(btnAdd, BorderLayout.EAST);

        JPanel editP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        editP.add(btnEdit);
        editP.add(btnDelete);

        JPanel south = new JPanel(new BorderLayout());
        south.add(addP, BorderLayout.NORTH);
        south.add(editP, BorderLayout.SOUTH);
        p.add(south, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            model.clear();
            getter.get().forEach(model::addElement);
        };
        btnAdd.addActionListener(e -> {
            if (!txt.getText().isEmpty()) {
                adder.accept(txt.getText());
                txt.setText("");
                refresh.run();
                refreshAllUIMetadata();
            }
        });
        btnEdit.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String newName = JOptionPane.showInputDialog(this, "Update Name:", selected);
                if (newName != null && !newName.isEmpty()) {
                    updater.accept(selected, newName);
                    refresh.run();
                    refreshAllUIMetadata();
                }
            }
        });
        btnDelete.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                if (JOptionPane.showConfirmDialog(this, "Delete " + selected + "?", "Confirm",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    deleter.accept(selected);
                    refresh.run();
                    refreshAllUIMetadata();
                }
            }
        });
        refresh.run();
        return p;
    }

    private JPanel createDiscountRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Pending Discount Requests (Approvals)");
        title.setFont(TITLE_FONT);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = { "ID", "Cashier", "Items", "Requested %", "Time" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnApprove = new JButton("Approve Discount");
        btnApprove.setBackground(new Color(46, 204, 113));
        btnApprove.setForeground(Color.WHITE);

        JButton btnReject = new JButton("Reject");
        btnReject.setBackground(new Color(231, 76, 60));
        btnReject.setForeground(Color.WHITE);

        bottom.add(btnApprove);
        bottom.add(btnReject);
        panel.add(bottom, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            model.setRowCount(0);
            drDAO.getPendingRequests()
                    .forEach(r -> model
                            .addRow(new Object[] { r.id, r.cashierName, r.itemsSummary, r.percentage + "%", r.time }));
        };

        btnApprove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                drDAO.updateStatus(id, "Approved");
                refresh.run();
                JOptionPane.showMessageDialog(this, "Discount Approved!");
            }
        });

        btnReject.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                drDAO.updateStatus(id, "Rejected");
                refresh.run();
                JOptionPane.showMessageDialog(this, "Discount Rejected.");
            }
        });

        refresh.run();
        new Timer(5000, e -> refresh.run()).start();

        return panel;
    }
}
