package com.citystyle.ui;

import com.citystyle.model.User;
import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends BaseDashboard {

    public CashierDashboard(User user) {
        super(user, "CityStyle Footwear Cashier POS");

        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR);

        // Add standard panels
        contentPanel.add(createInventoryPanel(), "Inventory");
        contentPanel.add(createSalesPanel(), "Sales");
        contentPanel.add(createReturnsPanel(), "Returns");

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

        JLabel line3 = new JLabel("CASHIER PANEL");
        line3.setForeground(new Color(46, 204, 113)); // Emerald Green
        line3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        line3.setAlignmentX(Component.LEFT_ALIGNMENT);
        line3.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        logoPanel.add(line1);
        logoPanel.add(line2);
        logoPanel.add(line3);
        sidebar.add(logoPanel);

        sidebar.add(createSidebarButton("Inventory / Search", "Inventory"));
        sidebar.add(createSidebarButton("Point of Sale (POS)", "Sales"));
        sidebar.add(createSidebarButton("Returns Request", "Returns"));

        sidebar.add(Box.createVerticalStrut(10));
        JButton adminLoginBtn = createSidebarButton("Admin Login", null);
        adminLoginBtn.setForeground(new Color(241, 196, 15)); // Gold Color
        adminLoginBtn.addActionListener(e -> {
            JDialog loginDialog = new JDialog(this, "Admin Approval Login", true);
            loginDialog.setSize(300, 200);
            loginDialog.setLayout(new GridLayout(3, 2, 5, 5));
            loginDialog.setLocationRelativeTo(this);

            JTextField txtUser = new JTextField();
            JPasswordField txtPass = new JPasswordField();
            JButton btnConfirm = new JButton("Login");

            loginDialog.add(new JLabel(" Username:"));
            loginDialog.add(txtUser);
            loginDialog.add(new JLabel(" Password:"));
            loginDialog.add(txtPass);
            loginDialog.add(new JLabel(""));
            loginDialog.add(btnConfirm);

            btnConfirm.addActionListener(ev -> {
                com.citystyle.model.User user = userDAO.login(txtUser.getText(), new String(txtPass.getPassword()));
                if (user != null && user.getRole().equalsIgnoreCase("Manager")) {
                    JOptionPane.showMessageDialog(loginDialog, "Admin Login Successful. Opening Admin Panel.");
                    new AdminDashboard(user).setVisible(true);
                    loginDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Invalid Admin Credentials", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            loginDialog.setVisible(true);
        });
        sidebar.add(adminLoginBtn);

        sidebar.add(Box.createVerticalGlue());
        JButton logoutBtn = createSidebarButton("Logout", null);
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(logoutBtn);

        return sidebar;
    }
}