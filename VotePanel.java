import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class VotePanel extends JPanel {
    private JComboBox<String> electionComboBox;
    private JComboBox<String> candidateComboBox;
    private JButton voteButton;
    private JButton backButton;
    private JLabel messageLabel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JSONArray elections;
    private JSONArray candidates;

    public VotePanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initializeComponents();
        setupLayout();
        setupListeners();
        loadElections();
    }

    private void initializeComponents() {
        electionComboBox = new JComboBox<>();
        candidateComboBox = new JComboBox<>();
        voteButton = new JButton("Cast Vote");
        backButton = new JButton("Back to Dashboard");
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.BLUE);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Select Election:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(electionComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Select Candidate:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(candidateComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(voteButton, gbc);

        gbc.gridy = 3;
        add(backButton, gbc);

        gbc.gridy = 4;
        add(messageLabel, gbc);
    }

    private void setupListeners() {
        electionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCandidates();
            }
        });

        voteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                castVote();
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
                elections = new JSONArray(response.body());
                electionComboBox.removeAllItems();

                for (int i = 0; i < elections.length(); i++) {
                    JSONObject election = elections.getJSONObject(i);
                    electionComboBox.addItem(election.getString("name") + " (ID: " + election.getLong("id") + ")");
                }

                if (elections.length() > 0) {
                    loadCandidates();
                }
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

    private void loadCandidates() {
        int selectedIndex = electionComboBox.getSelectedIndex();
        if (selectedIndex < 0 || elections == null) return;

        try {
            JSONObject selectedElection = elections.getJSONObject(selectedIndex);
            long electionId = selectedElection.getLong("id");

            HttpResponse<String> response = ApiClient.get("/elections/" + electionId + "/candidates");

            if (response.statusCode() == 200) {
                candidates = new JSONArray(response.body());
                candidateComboBox.removeAllItems();

                for (int i = 0; i < candidates.length(); i++) {
                    JSONObject candidate = candidates.getJSONObject(i);
                    candidateComboBox.addItem(candidate.getString("name") + " (ID: " + candidate.getLong("id") + ")");
                }
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText("Failed to load candidates: " + errorResponse.optString("message", "Unknown error"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Failed to load candidates: " + response.body());
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }

    private void castVote() {
        int electionIndex = electionComboBox.getSelectedIndex();
        int candidateIndex = candidateComboBox.getSelectedIndex();

        if (electionIndex < 0 || candidateIndex < 0) {
            messageLabel.setText("Please select both election and candidate");
            return;
        }

        try {
            JSONObject selectedElection = elections.getJSONObject(electionIndex);
            JSONObject selectedCandidate = candidates.getJSONObject(candidateIndex);

            long electionId = selectedElection.getLong("id");
            long candidateId = selectedCandidate.getLong("id");

            JSONObject voteData = new JSONObject();
            voteData.put("electionId", electionId);
            voteData.put("candidateId", candidateId);

            HttpResponse<String> response = ApiClient.post("/voting/vote", voteData.toString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Vote cast successfully!");
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText("Failed to cast vote: " + errorResponse.optString("message", "Unknown error"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Failed to cast vote: " + response.body());
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }
}
