import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardPanel extends JPanel {
    private JTextArea electionsArea;
    private JButton refreshButton;
    private JButton voteButton;
    private JButton logoutButton;
    private JButton verifyElectionButton;
    private JButton viewBulletinBoardButton;
    private JButton viewReceiptButton;
    private JLabel messageLabel;
    private JTextArea verificationArea;
    private JTextArea bulletinBoardArea;
    private JTextField electionIdField;
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
        electionsArea = new JTextArea(15, 50);
        electionsArea.setEditable(false);

        refreshButton = new JButton("Refresh Elections");
        voteButton = new JButton("Vote");
        logoutButton = new JButton("Logout");
        verifyElectionButton = new JButton("Verify Election");
        viewBulletinBoardButton = new JButton("View Bulletin Board");
        viewReceiptButton = new JButton("View Receipt");
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.BLUE);
        verificationArea = new JTextArea(8, 50);
        verificationArea.setEditable(false);
        bulletinBoardArea = new JTextArea(8, 50);
        bulletinBoardArea.setEditable(false);
        electionIdField = new JTextField(10);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(refreshButton);
        topPanel.add(voteButton);
        topPanel.add(logoutButton);

        // Verification panel
        JPanel verificationPanel = new JPanel(new BorderLayout());
        JPanel verificationInputPanel = new JPanel(new FlowLayout());
        verificationInputPanel.add(new JLabel("Election ID:"));
        verificationInputPanel.add(electionIdField);
        verificationInputPanel.add(verifyElectionButton);
        verificationInputPanel.add(viewBulletinBoardButton);
        verificationInputPanel.add(viewReceiptButton);

        verificationPanel.add(verificationInputPanel, BorderLayout.NORTH);
        verificationPanel.add(new JScrollPane(verificationArea), BorderLayout.CENTER);
        verificationPanel.setBorder(BorderFactory.createTitledBorder("Election Verification"));

        // Bulletin board panel
        JPanel bulletinPanel = new JPanel(new BorderLayout());
        bulletinPanel.add(new JScrollPane(bulletinBoardArea), BorderLayout.CENTER);
        bulletinPanel.setBorder(BorderFactory.createTitledBorder("Bulletin Board"));

        // Elections panel
        JPanel electionsPanel = new JPanel(new BorderLayout());
        electionsPanel.add(new JScrollPane(electionsArea), BorderLayout.CENTER);
        electionsPanel.setBorder(BorderFactory.createTitledBorder("Active Elections"));

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(electionsPanel, BorderLayout.NORTH);
        contentPanel.add(verificationPanel, BorderLayout.CENTER);
        contentPanel.add(bulletinPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
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

        verifyElectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyElectionResults();
            }
        });

        viewBulletinBoardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBulletinBoard();
            }
        });

        viewReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewReceipt();
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

        if (elections == null || elections.length() == 0) {
            sb.append("No active elections available.\n");
            electionsArea.setText(sb.toString());
            return;
        }

        for (int i = 0; i < elections.length(); i++) {
            try {
                JSONObject election = elections.getJSONObject(i);
                sb.append(String.format("ID: %d\n", election.optLong("id", 0)));
                sb.append(String.format("Name: %s\n", election.optString("name", "N/A")));
                sb.append(String.format("Description: %s\n", election.optString("description", "N/A")));
                sb.append(String.format("Start Date: %s\n", election.optString("startDate", "N/A")));
                sb.append(String.format("End Date: %s\n", election.optString("endDate", "N/A")));
                sb.append("---\n");
            } catch (Exception e) {
                sb.append(String.format("Error loading election %d: %s\n", i + 1, e.getMessage()));
                sb.append("---\n");
            }
        }

        electionsArea.setText(sb.toString());
    }

    private void verifyElectionResults() {
        String electionIdText = electionIdField.getText().trim();
        if (electionIdText.isEmpty()) {
            messageLabel.setText("Please enter an election ID to verify.");
            return;
        }

        try {
            long electionId = Long.parseLong(electionIdText);
            HttpResponse<String> response = ApiClient.get("/voting/results/election/" + electionId + "/verify");

            if (response.statusCode() == 200) {
                JSONObject verification = new JSONObject(response.body());
                StringBuilder result = new StringBuilder();
                result.append("=== ELECTION VERIFICATION RESULTS ===\n");
                result.append("Election ID: ").append(electionId).append("\n");
                result.append("Blockchain Verified: ").append(verification.optBoolean("blockchainVerified", false)).append("\n");
                result.append("Tally Verified: ").append(verification.optBoolean("tallyVerified", false)).append("\n");
                result.append("Total Votes: ").append(verification.optInt("totalVotes", 0)).append("\n");
                result.append("Verified Votes: ").append(verification.optInt("verifiedVotes", 0)).append("\n");

                if (verification.has("candidateResults")) {
                    JSONArray candidates = verification.getJSONArray("candidateResults");
                    result.append("\nCandidate Results:\n");
                    for (int i = 0; i < candidates.length(); i++) {
                        JSONObject candidate = candidates.getJSONObject(i);
                        result.append("Candidate ").append(candidate.optLong("candidateId", 0))
                              .append(": ").append(candidate.optInt("votes", 0)).append(" votes\n");
                    }
                }

                verificationArea.setText(result.toString());
                messageLabel.setText("Election verification completed!");
            } else {
                messageLabel.setText("Failed to verify election: " + response.body());
            }
        } catch (NumberFormatException nfe) {
            messageLabel.setText("Invalid election ID format.");
        } catch (Exception ex) {
            messageLabel.setText("Connection error during verification: " + ex.getMessage());
        }
    }

    private void viewBulletinBoard() {
        String electionIdText = electionIdField.getText().trim();
        if (electionIdText.isEmpty()) {
            messageLabel.setText("Please enter an election ID to view bulletin board.");
            return;
        }

        try {
            long electionId = Long.parseLong(electionIdText);
            HttpResponse<String> response = ApiClient.get("/voting/bulletin-board/election/" + electionId);

            if (response.statusCode() == 200) {
                JSONArray bulletinBoard = new JSONArray(response.body());
                StringBuilder bulletin = new StringBuilder();
                bulletin.append("=== BULLETIN BOARD ===\n");
                bulletin.append("Election ID: ").append(electionId).append("\n\n");

                for (int i = 0; i < bulletinBoard.length(); i++) {
                    JSONObject entry = bulletinBoard.getJSONObject(i);
                    bulletin.append("Vote Hash: ").append(entry.optString("voteHash", "N/A")).append("\n");
                    bulletin.append("Candidate ID: ").append(entry.optLong("candidateId", 0)).append("\n");
                    bulletin.append("Timestamp: ").append(entry.optString("timestamp", "N/A")).append("\n");
                    bulletin.append("Verified: ").append(entry.optBoolean("verified", false)).append("\n");
                    bulletin.append("------------------------\n");
                }

                bulletinBoardArea.setText(bulletin.toString());
                messageLabel.setText("Bulletin board loaded successfully!");
            } else {
                messageLabel.setText("Failed to load bulletin board: " + response.body());
            }
        } catch (NumberFormatException nfe) {
            messageLabel.setText("Invalid election ID format.");
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }

    private void viewReceipt() {
        // For demonstration, we'll show a dialog to enter vote hash
        // In a real application, this would be linked to user's votes
        String voteHash = JOptionPane.showInputDialog(this, "Enter Vote Hash to verify receipt:");
        if (voteHash == null || voteHash.trim().isEmpty()) {
            return;
        }

        try {
            HttpResponse<String> response = ApiClient.get("/voting/verify/" + voteHash.trim());

            if (response.statusCode() == 200) {
                JSONObject verification = new JSONObject(response.body());
                boolean isValid = verification.optBoolean("isValid", false);

                StringBuilder receipt = new StringBuilder();
                receipt.append("=== RECEIPT VERIFICATION ===\n");
                receipt.append("Vote Hash: ").append(voteHash).append("\n");
                receipt.append("Valid: ").append(isValid ? "YES" : "NO").append("\n");

                if (verification.has("voteDetails")) {
                    JSONObject details = verification.getJSONObject("voteDetails");
                    receipt.append("Election ID: ").append(details.optLong("electionId", 0)).append("\n");
                    receipt.append("Candidate ID: ").append(details.optLong("candidateId", 0)).append("\n");
                    receipt.append("Transaction Hash: ").append(details.optString("transactionHash", "N/A")).append("\n");
                    receipt.append("Block Number: ").append(details.optLong("blockNumber", 0)).append("\n");
                    receipt.append("Voted At: ").append(details.optString("votedAt", "N/A")).append("\n");
                }

                JOptionPane.showMessageDialog(this, receipt.toString(),
                    "Receipt Verification", JOptionPane.INFORMATION_MESSAGE);

                messageLabel.setText(isValid ? "Receipt verification successful!" : "Receipt verification failed!");
            } else {
                messageLabel.setText("Failed to verify receipt: " + response.body());
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error during receipt verification: " + ex.getMessage());
        }
    }
}
