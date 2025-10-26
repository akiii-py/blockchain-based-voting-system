import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminPanel extends JPanel {
    private JTextField electionNameField;
    private JTextArea electionDescriptionArea;
    private JButton createElectionButton;
    private JButton viewElectionsButton;
    private JButton logoutButton;
    private JTextArea resultsArea;
    private JLabel messageLabel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField candidateNameField;
    private JComboBox<String> electionComboBox;
    private JButton addCandidateButton;
    private JSONArray elections;

    public AdminPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadElectionsForCandidates();
    }

    private void initializeComponents() {
        electionNameField = new JTextField(30);
        electionDescriptionArea = new JTextArea(3, 30);
        electionDescriptionArea.setLineWrap(true);
        createElectionButton = new JButton("Create Election");
        viewElectionsButton = new JButton("View All Elections");
        logoutButton = new JButton("Logout");
        resultsArea = new JTextArea(20, 50);
        resultsArea.setEditable(false);
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.BLUE);
        candidateNameField = new JTextField(30);
        electionComboBox = new JComboBox<>();
        addCandidateButton = new JButton("Add Candidate");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Election creation section
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Election Name:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(electionNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JScrollPane(electionDescriptionArea), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        inputPanel.add(createElectionButton, gbc);

        // Candidate addition section
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Select Election:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(electionComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Candidate Name:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(candidateNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        inputPanel.add(addCandidateButton, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewElectionsButton);
        buttonPanel.add(logoutButton);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(resultsArea), BorderLayout.SOUTH);
        add(messageLabel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        createElectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createElection();
            }
        });

        viewElectionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllElections();
            }
        });

        addCandidateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCandidate();
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

    private void createElection() {
        String name = electionNameField.getText().trim();
        String description = electionDescriptionArea.getText().trim();

        if (name.isEmpty()) {
            messageLabel.setText("Please enter election name");
            return;
        }

        try {
            JSONObject electionData = new JSONObject();
            electionData.put("name", name);
            electionData.put("description", description);

            HttpResponse<String> response = ApiClient.post("/admin/elections", electionData.toString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Election created successfully!");
                clearFields();
                loadElectionsForCandidates(); // Refresh elections list for candidate addition
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText("Failed to create election: " + errorResponse.optString("message", "Unknown error"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Failed to create election: " + response.body());
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }

    private void viewAllElections() {
        try {
            HttpResponse<String> response = ApiClient.get("/elections/active");

            if (response.statusCode() == 200) {
                JSONArray elections = new JSONArray(response.body());
                StringBuilder sb = new StringBuilder();
                sb.append("All Elections:\n\n");

                for (int i = 0; i < elections.length(); i++) {
                    JSONObject election = elections.getJSONObject(i);
                    sb.append(String.format("ID: %d\n", election.getLong("id")));
                    sb.append(String.format("Name: %s\n", election.getString("name")));
                    sb.append(String.format("Description: %s\n", election.optString("description", "N/A")));
                    sb.append(String.format("Active: %s\n", election.optBoolean("active", false)));
                    sb.append("---\n");
                }

                resultsArea.setText(sb.toString());
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

    private void clearFields() {
        electionNameField.setText("");
        electionDescriptionArea.setText("");
    }

    private void addCandidate() {
        String candidateName = candidateNameField.getText().trim();
        int selectedIndex = electionComboBox.getSelectedIndex();

        if (candidateName.isEmpty()) {
            messageLabel.setText("Please enter candidate name");
            return;
        }

        if (selectedIndex < 0 || elections == null) {
            messageLabel.setText("Please select an election");
            return;
        }

        try {
            JSONObject selectedElection = elections.getJSONObject(selectedIndex);
            long electionId = selectedElection.getLong("id");

            JSONObject candidateData = new JSONObject();
            candidateData.put("name", candidateName);
            candidateData.put("electionId", electionId);

            HttpResponse<String> response = ApiClient.post("/admin/elections/" + electionId + "/candidates", candidateData.toString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Candidate added successfully!");
                candidateNameField.setText("");
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText("Failed to add candidate: " + errorResponse.optString("message", "Unknown error"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Failed to add candidate: " + response.body());
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }

    private void loadElectionsForCandidates() {
        try {
            HttpResponse<String> response = ApiClient.get("/elections/active");

            if (response.statusCode() == 200) {
                elections = new JSONArray(response.body());
                electionComboBox.removeAllItems();

                for (int i = 0; i < elections.length(); i++) {
                    JSONObject election = elections.getJSONObject(i);
                    electionComboBox.addItem(election.getString("name") + " (ID: " + election.getLong("id") + ")");
                }
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText("Failed to load elections for candidates: " + errorResponse.optString("message", "Unknown error"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Failed to load elections for candidates: " + response.body());
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }
}
