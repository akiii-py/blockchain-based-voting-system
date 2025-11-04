package com.evoting.swingclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.evoting.swingclient.EVotingClient;
import com.evoting.swingclient.api.AuthService;

public class LoginPanel extends JPanel {
    private EVotingClient parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    
    public LoginPanel(EVotingClient parent) {
        this.parent = parent;
        initializeComponents();
        layoutComponents();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.addActionListener(new LoginActionListener());
        
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> parent.showRegisterPanel());
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
    }
    
    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Blockchain E-Voting System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        add(subtitleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Username:"), gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        add(usernameField, gbc);
        
        // Password
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Password:"), gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        add(passwordField, gbc);
        
        // Status label
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(statusLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridy = 6;
        add(buttonPanel, gbc);
    }
    
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter both username and password");
                return;
            }
            
            loginButton.setEnabled(false);
            statusLabel.setText("Logging in...");
            statusLabel.setForeground(Color.BLUE);
            
            SwingUtilities.invokeLater(() -> {
                try {
                    AuthService.LoginResponse response = AuthService.login(username, password);
                    parent.setCurrentUser(response.user, response.token);
                    statusLabel.setText("Login successful!");
                    statusLabel.setForeground(Color.GREEN);
                } catch (Exception ex) {
                    statusLabel.setText("Login failed: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                    loginButton.setEnabled(true);
                }
            });
        }
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
