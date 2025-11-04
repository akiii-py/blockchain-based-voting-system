package com.evoting.swingclient;

import javax.swing.*;
import java.awt.*;
import com.evoting.swingclient.ui.LoginPanel;
import com.evoting.swingclient.ui.RegisterPanel;
import com.evoting.swingclient.ui.DashboardPanel;
import com.evoting.swingclient.ui.AdminPanel;
import com.evoting.swingclient.model.User;

public class EVotingClient extends JFrame {
    private static final String API_BASE_URL = "http://localhost:8080/api";
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private AdminPanel adminPanel;
    
    private User currentUser;
    private String authToken;
    
    public EVotingClient() {
        initializeComponents();
        setupFrame();
        showLoginPanel();
    }
    
    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        dashboardPanel = new DashboardPanel(this);
        adminPanel = new AdminPanel(this);
        
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(adminPanel, "ADMIN");
    }
    
    private void setupFrame() {
        setTitle("Blockchain E-Voting System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void showRegisterPanel() {
        cardLayout.show(mainPanel, "REGISTER");
    }
    
    public void showDashboardPanel() {
        dashboardPanel.refresh();
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    
    public void showAdminPanel() {
        adminPanel.refresh();
        cardLayout.show(mainPanel, "ADMIN");
    }
    
    public void setCurrentUser(User user, String token) {
        this.currentUser = user;
        this.authToken = token;
        
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            showAdminPanel();
        } else {
            showDashboardPanel();
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void logout() {
        currentUser = null;
        authToken = null;
        showLoginPanel();
    }
    
    public static String getApiBaseUrl() {
        return API_BASE_URL;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EVotingClient().setVisible(true);
        });
    }
}
