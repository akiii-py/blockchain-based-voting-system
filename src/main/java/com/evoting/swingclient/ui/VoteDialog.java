package com.evoting.swingclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import com.evoting.swingclient.EVotingClient;
import com.evoting.swingclient.model.Election;
import com.evoting.swingclient.model.Candidate;
import com.evoting.swingclient.model.Vote;
import com.evoting.swingclient.api.ElectionService;
import com.evoting.swingclient.api.VotingService;

public class VoteDialog extends JDialog {
    private EVotingClient parent;
    private Election election;
    private List<Candidate> candidates;
    private ButtonGroup candidateGroup;
    private JPanel candidatesPanel;
    private JButton submitButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    public VoteDialog(EVotingClient parent, Election election) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), "Cast Your Vote", true);
        this.parent = parent;
        this.election = election;
        
        initializeComponents();
        layoutComponents();
        loadCandidates();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initializeComponents() {
        candidateGroup = new ButtonGroup();
        candidatesPanel = new JPanel();
        candidatesPanel.setLayout(new BoxLayout(candidatesPanel, BoxLayout.Y_AXIS));
        candidatesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        submitButton = new JButton("Submit Vote");
        submitButton.setPreferredSize(new Dimension(120, 35));
        submitButton.addActionListener(new SubmitVoteListener());
        submitButton.setEnabled(false);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());
        
        statusLabel = new JLabel("Loading candidates...");
        statusLabel.setForeground(Color.BLUE);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel("<html><h2>" + election.getTitle() + "</h2></html>");
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(candidatesPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadCandidates() {
        SwingWorker<List<Candidate>, Void> worker = new SwingWorker<List<Candidate>, Void>() {
            @Override
            protected List<Candidate> doInBackground() throws Exception {
                return ElectionService.getCandidates(election.getId(), parent.getAuthToken());
            }
            
            @Override
            protected void done() {
                try {
                    candidates = get();
                    candidatesPanel.removeAll();
                    
                    if (candidates == null || candidates.isEmpty()) {
                        statusLabel.setText("No candidates available for this election");
                        statusLabel.setForeground(Color.RED);
                    } else {
                        for (Candidate candidate : candidates) {
                            JPanel candidatePanel = createCandidatePanel(candidate);
                            candidatesPanel.add(candidatePanel);
                        }
                        statusLabel.setText("Select a candidate and click Submit Vote");
                        statusLabel.setForeground(Color.BLACK);
                        submitButton.setEnabled(true);
                    }
                    candidatesPanel.revalidate();
                    candidatesPanel.repaint();
                } catch (Exception e) {
                    statusLabel.setText("Error loading candidates: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }
    
    private JPanel createCandidatePanel(Candidate candidate) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JRadioButton radioButton = new JRadioButton();
        radioButton.addActionListener(e -> submitButton.setEnabled(true));
        candidateGroup.add(radioButton);
        panel.add(radioButton, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        JLabel nameLabel = new JLabel("<html><b>" + candidate.getName() + "</b></html>");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(nameLabel);
        
        JLabel partyLabel = new JLabel("Party: " + candidate.getParty());
        infoPanel.add(partyLabel);
        
        if (candidate.getDescription() != null && !candidate.getDescription().isEmpty()) {
            JLabel descLabel = new JLabel("<html>" + candidate.getDescription() + "</html>");
            descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            infoPanel.add(descLabel);
        }
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Store candidate ID in radio button
        radioButton.setActionCommand(String.valueOf(candidate.getId()));
        
        return panel;
    }
    
    private class SubmitVoteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (parent.getAuthToken() == null || parent.getCurrentUser() == null) {
                JOptionPane.showMessageDialog(VoteDialog.this,
                    "You must be logged in to vote. Please log in again.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE);
                dispose();
                parent.logout();
                return;
            }
            ButtonModel selected = candidateGroup.getSelection();
            if (selected == null) {
                JOptionPane.showMessageDialog(VoteDialog.this, 
                    "Please select a candidate", 
                    "No Candidate Selected", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(
                VoteDialog.this,
                "Are you sure you want to vote for this candidate? This action cannot be undone.",
                "Confirm Vote",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            Long candidateId = Long.parseLong(selected.getActionCommand());
            submitButton.setEnabled(false);
            statusLabel.setText("Submitting vote...");
            statusLabel.setForeground(Color.BLUE);
            
            SwingWorker<Vote, Void> worker = new SwingWorker<Vote, Void>() {
                @Override
                protected Vote doInBackground() throws Exception {
                    Long userId = parent.getCurrentUser() != null ? parent.getCurrentUser().getId() : null;
                    try {
                        if (userId != null) {
                            return VotingService.castVote(userId, candidateId, election.getId(), parent.getAuthToken());
                        }
                    } catch (Exception ex) {
                        // fallback below
                    }
                    return VotingService.castVote(candidateId, election.getId(), parent.getAuthToken());
                }
                
                @Override
                protected void done() {
                    try {
                        Vote vote = get();
                        dispose();
                        new ReceiptDialog(parent, vote);
                    } catch (Exception ex) {
                        statusLabel.setText("Error submitting vote: " + ex.getMessage());
                        statusLabel.setForeground(Color.RED);
                        submitButton.setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }
}
