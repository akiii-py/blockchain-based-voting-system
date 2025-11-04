package com.evoting.swingclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.evoting.swingclient.EVotingClient;
import com.evoting.swingclient.model.Vote;

public class ReceiptDialog extends JDialog {
    private EVotingClient parent;
    private Vote vote;
    
    public ReceiptDialog(EVotingClient parent, Vote vote) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), "Vote Receipt", true);
        this.parent = parent;
        this.vote = vote;
        
        initializeComponents();
        layoutComponents();
        
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Components will be created in layoutComponents
    }
    
    private void layoutComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Success message
        JLabel successLabel = new JLabel("<html><h2 style='color: green;'>✓ Vote Successfully Cast!</h2></html>");
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(successLabel, BorderLayout.NORTH);
        
        // Receipt details
        JPanel receiptPanel = new JPanel(new GridBagLayout());
        receiptPanel.setBorder(BorderFactory.createTitledBorder("Cryptographic Receipt"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        addReceiptField(receiptPanel, gbc, "Vote ID:", String.valueOf(vote.getId()), row++);
        addReceiptField(receiptPanel, gbc, "Election ID:", String.valueOf(vote.getElectionId()), row++);
        addReceiptField(receiptPanel, gbc, "Candidate ID:", String.valueOf(vote.getCandidateId()), row++);
        addReceiptField(receiptPanel, gbc, "Voted At:", vote.getVotedAt() != null ? vote.getVotedAt().toString() : "N/A", row++);
        
        if (vote.getTransactionHash() != null) {
            addReceiptField(receiptPanel, gbc, "Transaction Hash:", vote.getTransactionHash(), row++);
        }
        
        if (vote.getBlockNumber() != null) {
            addReceiptField(receiptPanel, gbc, "Block Number:", String.valueOf(vote.getBlockNumber()), row++);
        }
        
        if (vote.getVoteHash() != null) {
            JTextArea voteHashArea = new JTextArea(vote.getVoteHash(), 2, 50);
            voteHashArea.setEditable(false);
            voteHashArea.setWrapStyleWord(true);
            voteHashArea.setLineWrap(true);
            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            receiptPanel.add(new JLabel("Vote Hash:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            receiptPanel.add(new JScrollPane(voteHashArea), gbc);
        }
        
        if (vote.getReceiptSignature() != null) {
            JTextArea signatureArea = new JTextArea(vote.getReceiptSignature(), 3, 50);
            signatureArea.setEditable(false);
            signatureArea.setWrapStyleWord(true);
            signatureArea.setLineWrap(true);
            gbc.gridx = 0;
            gbc.gridy = row++;
            receiptPanel.add(new JLabel("Receipt Signature:"), gbc);
            gbc.gridx = 1;
            receiptPanel.add(new JScrollPane(signatureArea), gbc);
        }
        
        if (vote.getPublicKey() != null) {
            JTextArea publicKeyArea = new JTextArea(vote.getPublicKey(), 3, 50);
            publicKeyArea.setEditable(false);
            publicKeyArea.setWrapStyleWord(true);
            publicKeyArea.setLineWrap(true);
            gbc.gridx = 0;
            gbc.gridy = row++;
            receiptPanel.add(new JLabel("Public Key (for verification):"), gbc);
            gbc.gridx = 1;
            receiptPanel.add(new JScrollPane(publicKeyArea), gbc);
        }
        
        JScrollPane scrollPane = new JScrollPane(receiptPanel);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Warning and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JLabel warningLabel = new JLabel("<html><small style='color: red;'>Important: Save this receipt for verification. Your vote is now recorded on the blockchain.</small></html>");
        bottomPanel.add(warningLabel, BorderLayout.NORTH);
        
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(120, 35));
        okButton.addActionListener(e -> {
            dispose();
            parent.showDashboardPanel();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(contentPanel);
    }
    
    private void addReceiptField(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        panel.add(valueLabel, gbc);
    }
}
