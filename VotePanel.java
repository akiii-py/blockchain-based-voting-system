import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class VotePanel extends JPanel {
    private JTextField electionIdField;
    private JTextField candidateIdField;
    private JButton voteButton;
    private JButton backButton;
    private JButton verifyReceiptButton;
    private JButton downloadReceiptButton;
    private JButton viewBulletinBoardButton;
    private JLabel messageLabel;
    private JTextArea receiptArea;
    private JTextArea bulletinBoardArea;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JSONObject currentVoteReceipt;

    public VotePanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadElections();
    }

    private void initializeComponents() {
        electionIdField = new JTextField(10);
        candidateIdField = new JTextField(10);
        voteButton = new JButton("Cast Vote");
        backButton = new JButton("Back to Dashboard");
        verifyReceiptButton = new JButton("Verify Receipt");
        downloadReceiptButton = new JButton("Download Receipt");
        viewBulletinBoardButton = new JButton("View Bulletin Board");
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.BLUE);
        receiptArea = new JTextArea(10, 40);
        receiptArea.setEditable(false);
        bulletinBoardArea = new JTextArea(10, 40);
        bulletinBoardArea.setEditable(false);
        currentVoteReceipt = null;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Election ID:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(electionIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Candidate ID:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(candidateIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        inputPanel.add(voteButton, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(verifyReceiptButton);
        buttonPanel.add(downloadReceiptButton);
        buttonPanel.add(viewBulletinBoardButton);
        buttonPanel.add(backButton);

        // Receipt panel
        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(BorderFactory.createTitledBorder("Vote Receipt"));
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        // Bulletin board panel
        JPanel bulletinPanel = new JPanel(new BorderLayout());
        bulletinPanel.setBorder(BorderFactory.createTitledBorder("Bulletin Board"));
        bulletinPanel.add(new JScrollPane(bulletinBoardArea), BorderLayout.CENTER);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(receiptPanel, BorderLayout.CENTER);
        contentPanel.add(bulletinPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(messageLabel, BorderLayout.NORTH);
    }

    private void setupListeners() {
        voteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                castVote();
            }
        });

        verifyReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyReceipt();
            }
        });

        downloadReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadReceipt();
            }
        });

        viewBulletinBoardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBulletinBoard();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "DASHBOARD");
            }
        });
    }

    private void loadElections() {
        try {
            HttpResponse<String> response = ApiClient.get("/elections/active");

            if (response.statusCode() == 200) {
                JSONArray elections = new JSONArray(response.body());

                if (elections.length() == 0) {
                    messageLabel.setText("No active elections available.");
                    return;
                }

                StringBuilder electionList = new StringBuilder("Available Elections:\n");
                for (int i = 0; i < elections.length(); i++) {
                    try {
                        JSONObject election = elections.getJSONObject(i);
                        String title = election.optString("title", "NA");
                        long id = election.optLong("id", 0);
                        electionList.append("ID: ").append(id).append(" - ").append(title).append("\n");
                    } catch (Exception e) {
                        electionList.append("ID: Unknown - Error loading election\n");
                    }
                }
                messageLabel.setText(electionList.toString());
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



    private void castVote() {
        String electionIdText = electionIdField.getText().trim();
        String candidateIdText = candidateIdField.getText().trim();

        if (electionIdText.isEmpty() || candidateIdText.isEmpty()) {
            messageLabel.setText("Please enter both election ID and candidate ID");
            return;
        }

        try {
            long electionId = Long.parseLong(electionIdText);
            long candidateId = Long.parseLong(candidateIdText);

            JSONObject voteData = new JSONObject();
            voteData.put("electionId", electionId);
            voteData.put("candidateId", candidateId);

            HttpResponse<String> response = ApiClient.post("/voting/vote", voteData.toString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Vote cast successfully!");
                JSONObject voteResponse = new JSONObject(response.body());
                currentVoteReceipt = voteResponse;
                displayVoteReceipt(voteResponse);
                electionIdField.setText("");
                candidateIdField.setText("");
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText("Failed to cast vote: " + errorResponse.optString("message", "Unknown error"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Failed to cast vote: " + response.body());
                }
            }
        } catch (NumberFormatException nfe) {
            messageLabel.setText("Invalid ID format. Please enter numeric IDs only.");
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }

    private void displayVoteReceipt(JSONObject vote) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== VOTE RECEIPT ===\n");
        receipt.append("Vote ID: ").append(vote.optLong("id", 0)).append("\n");
        receipt.append("Election ID: ").append(vote.optLong("electionId", 0)).append("\n");
        receipt.append("Candidate ID: ").append(vote.optLong("candidateId", 0)).append("\n");
        receipt.append("Transaction Hash: ").append(vote.optString("transactionHash", "N/A")).append("\n");
        receipt.append("Block Number: ").append(vote.optLong("blockNumber", 0)).append("\n");
        receipt.append("Vote Hash: ").append(vote.optString("voteHash", "N/A")).append("\n");
        receipt.append("Voted At: ").append(vote.optString("votedAt", "N/A")).append("\n");
        receipt.append("Verified: ").append(vote.optBoolean("isVerified", false)).append("\n");
        receipt.append("===================\n");

        receiptArea.setText(receipt.toString());
    }

    private void verifyReceipt() {
        if (currentVoteReceipt == null) {
            messageLabel.setText("No vote receipt available. Please cast a vote first.");
            return;
        }

        try {
            String voteHash = currentVoteReceipt.optString("voteHash", "");
            if (voteHash.isEmpty()) {
                messageLabel.setText("No vote hash found in receipt.");
                return;
            }

            HttpResponse<String> response = ApiClient.get("/voting/verify/" + voteHash);

            if (response.statusCode() == 200) {
                JSONObject verification = new JSONObject(response.body());
                boolean isValid = verification.optBoolean("isValid", false);
                messageLabel.setForeground(isValid ? Color.GREEN : Color.RED);
                messageLabel.setText(isValid ? "Receipt verification successful!" : "Receipt verification failed!");
            } else {
                messageLabel.setText("Failed to verify receipt: " + response.body());
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error during verification: " + ex.getMessage());
        }
    }

    private void downloadReceipt() {
        if (currentVoteReceipt == null) {
            messageLabel.setText("No vote receipt available. Please cast a vote first.");
            return;
        }

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("vote_receipt_" + currentVoteReceipt.optLong("id", 0) + ".txt"));
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(receiptArea.getText());
                    messageLabel.setForeground(Color.GREEN);
                    messageLabel.setText("Receipt downloaded successfully!");
                }
            }
        } catch (IOException ex) {
            messageLabel.setText("Error downloading receipt: " + ex.getMessage());
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
}
