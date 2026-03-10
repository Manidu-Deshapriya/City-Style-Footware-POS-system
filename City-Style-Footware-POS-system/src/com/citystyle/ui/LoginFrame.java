package com.citystyle.ui;

import com.citystyle.dao.UserDAO;
import com.citystyle.model.User;
import java.awt.GridLayout;
import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("CityStyle Footwear Login");
        setSize(300, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JButton btnLogin = new JButton("Login");

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel p1 = new JPanel(new GridLayout(4, 1, 5, 5));
        p1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        p1.add(new JLabel("Username:"));
        p1.add(txtUser);
        p1.add(new JLabel("Password:"));
        p1.add(txtPass);

        add(p1);
        add(btnLogin);

        Runnable doLogin = () -> {
            UserDAO dao = new UserDAO();
            User user = dao.login(txtUser.getText(), new String(txtPass.getPassword()));

            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful! Role: " + user.getRole());

                // Logic division: Route to separate Dashboard files
                if (user.getRole().equalsIgnoreCase("Manager")) {
                    new AdminDashboard(user).setVisible(true);
                } else {
                    new CashierDashboard(user).setVisible(true);
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        };

        btnLogin.addActionListener(e -> doLogin.run());
        txtUser.addActionListener(e -> doLogin.run());
        txtPass.addActionListener(e -> doLogin.run());
    }
}