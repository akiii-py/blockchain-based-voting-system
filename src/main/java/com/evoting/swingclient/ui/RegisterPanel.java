package com.evoting.swingclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.evoting.swingclient.EVotingClient;
import com.evoting.swingclient.api.AuthService;

public class RegisterPanel extends JPanel {
    private EVotingClient parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField voterIdField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel statusLabel;
    
    public RegisterPanel(EVotingClient parent) {
        this.parent = parent;
        initializeComponents();
        layoutComponents();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(25);
        passwordField = new JPasswordField(25);
        emailField = new JTextField(25);
        fullNameField = new JTextField(25);
        voterIdField = new JTextField(25);
        
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(new RegisterActionListener());
        
        backButton = new JButton("Back to Login");
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.addActionListener(e -> {
            clearFields();
            parent.showLoginPanel();
        });
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
    }
    
    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.EAST;
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        
        int row = 2;
        gbc.gridx = 0;
        gbc.gridy = row++;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = row++;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = row++;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = row++;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(fullNameField, gbc);
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = row++;
        add(new JLabel("Voter ID:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(voterIdField, gbc);
        
        // Status label
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(statusLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridy = row;
        add(buttonPanel, gbc);
    }
    
    private class RegisterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String voterId = voterIdField.getText().trim();
            
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || 
                fullName.isEmpty() || voterId.isEmpty()) {
                statusLabel.setText("Please fill in all fields");
                return;
            }
            
            registerButton.setEnabled(false);
            statusLabel.setText("Registering...");
            statusLabel.setForeground(Color.BLUE);
            
            SwingUtilities.invokeLater(() -> {
                try {
                    AuthService.register(username, password, email, fullName, voterId);
                    statusLabel.setText("Registration successful! Please login.");
                    statusLabel.setForeground(Color.GREEN);
                    Timer timer = new Timer(2000, ev -> {
                        clearFields();
                        parent.showLoginPanel();
                    });
                    timer.setRepeats(false);
                    timer.start();
                } catch (Exception ex) {
                    statusLabel.setText("Registration failed: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                    registerButton.setEnabled(true);
                }
            });
        }
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        fullNameField.setText("");
        voterIdField.setText("");
        statusLabel.setText(" ");
    }
}
