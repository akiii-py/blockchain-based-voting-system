import javax.swing.*;
import java.awt.*;

public class SwingVotingApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public SwingVotingApp() {
        initializeFrame();
        setupCardLayout();
        addPanels();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Blockchain E-Voting System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupCardLayout() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }

    private void addPanels() {
        LoginPanel loginPanel = new LoginPanel(cardLayout, mainPanel);
        RegisterPanel registerPanel = new RegisterPanel(cardLayout, mainPanel);
        DashboardPanel dashboardPanel = new DashboardPanel(cardLayout, mainPanel);
        VotePanel votePanel = new VotePanel(cardLayout, mainPanel);
        AdminPanel adminPanel = new AdminPanel(cardLayout, mainPanel);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(votePanel, "VOTE");
        mainPanel.add(adminPanel, "ADMIN");

        cardLayout.show(mainPanel, "LOGIN");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingVotingApp());
    }
}
