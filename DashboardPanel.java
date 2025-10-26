import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardPanel extends JPanel {
    private JTextArea electionsArea;
    private JButton refreshButton;
    private JButton voteButton;
    private JButton logoutButton;
    private JLabel messageLabel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JSONArray elections;

    public DashboardPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadElections();
    }

    private void initializeComponents() {
        electionsArea = new JTextArea(20, 50);
        electionsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(electionsArea);

        refreshButton = new JButton("Refresh Elections");
        voteButton = new JButton("Vote");
        logoutButton = new JButton("Logout");
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.BLUE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(refreshButton);
        topPanel.add(voteButton);
        topPanel.add(logoutButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(electionsArea), BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadElections();
            }
        });

        voteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "VOTE");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiClient.clearJwtToken();
                cardLayout.show(mainPanel, "LOGIN");
            }
        });
    }

    private void loadElections() {
        try {
            HttpResponse<String> response = ApiClient.get("/elections/active");

            if (response.statusCode() == 200) {
                elections = new JSONArray(response.body());
                displayElections();
                messageLabel.setText("Elections loaded successfully");
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText("Failed to load elections: " + errorResponse.optString("message", "Unknown error"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Failed to load elections: " + response.body());
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }

    private void displayElections() {
        StringBuilder sb = new StringBuilder();
        sb.append("Active Elections:\n\n");

        for (int i = 0; i < elections.length(); i++) {
            JSONObject election = elections.getJSONObject(i);
            sb.append(String.format("ID: %d\n", election.getLong("id")));
            sb.append(String.format("Name: %s\n", election.getString("name")));
            sb.append(String.format("Description: %s\n", election.optString("description", "N/A")));
            sb.append(String.format("Start Date: %s\n", election.optString("startDate", "N/A")));
            sb.append(String.format("End Date: %s\n", election.optString("endDate", "N/A")));
            sb.append("---\n");
        }

        electionsArea.setText(sb.toString());
    }
}
