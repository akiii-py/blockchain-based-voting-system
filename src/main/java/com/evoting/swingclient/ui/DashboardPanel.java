package com.evoting.swingclient.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import com.evoting.swingclient.EVotingClient;
import com.evoting.swingclient.model.Election;
import com.evoting.swingclient.api.ElectionService;
import com.evoting.swingclient.api.VotingService;

public class DashboardPanel extends JPanel {
    private EVotingClient parent;
    private JTable electionsTable;
    private DefaultTableModel tableModel;
    private JButton voteButton;
    private JButton refreshButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;
    private JLabel statusLabel;
    private List<Election> currentElections;
    
    public DashboardPanel(EVotingClient parent) {
        this.parent = parent;
        initializeComponents();
        layoutComponents();
    }
    
    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + (parent.getCurrentUser() != null ? parent.getCurrentUser().getFullName() : "User"));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Description", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        electionsTable = new JTable(tableModel);
        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        electionsTable.setRowHeight(25);
        
        voteButton = new JButton("Vote");
        voteButton.setPreferredSize(new Dimension(120, 35));
        voteButton.addActionListener(new VoteActionListener());
        
        refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.addActionListener(e -> refresh());
        
        logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(120, 35));
        logoutButton.addActionListener(e -> parent.logout());
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Elections table
        JScrollPane scrollPane = new JScrollPane(electionsTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(voteButton);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void refresh() {
        if (parent.getAuthToken() == null || parent.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "Your session has expired. Please log in again.", "Login Required", JOptionPane.WARNING_MESSAGE);
            parent.logout();
            return;
        }
        statusLabel.setText("Loading elections...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<List<Election>, Void> worker = new SwingWorker<List<Election>, Void>() {
            @Override
            protected List<Election> doInBackground() throws Exception {
                return ElectionService.getActiveElections(parent.getAuthToken());
            }
            
            @Override
            protected void done() {
                try {
                    currentElections = get();
                    tableModel.setRowCount(0);
                    
                    if (currentElections == null || currentElections.isEmpty()) {
                        statusLabel.setText("No active elections available");
                        statusLabel.setForeground(Color.ORANGE);
                    } else {
                        for (Election election : currentElections) {
                            tableModel.addRow(new Object[]{
                                election.getId(),
                                election.getTitle(),
                                election.getDescription(),
                                election.isActive() ? "Active" : "Inactive"
                            });
                        }
                        statusLabel.setText("Loaded " + currentElections.size() + " election(s)");
                        statusLabel.setForeground(Color.GREEN);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error loading elections: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }
    
    private class VoteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = electionsTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(DashboardPanel.this, 
                    "Please select an election to vote", 
                    "No Election Selected", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Election selectedElection = currentElections.get(selectedRow);
            if (!selectedElection.isActive()) {
                JOptionPane.showMessageDialog(DashboardPanel.this, 
                    "This election is not active", 
                    "Election Not Active", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check if user already voted
            SwingWorker<Boolean, Void> checkWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return VotingService.hasUserVoted(
                        parent.getCurrentUser().getId(), 
                        selectedElection.getId(), 
                        parent.getAuthToken()
                    );
                }
                
                @Override
                protected void done() {
                    try {
                        boolean hasVoted = get();
                        if (hasVoted) {
                            JOptionPane.showMessageDialog(DashboardPanel.this, 
                                "You have already voted in this election", 
                                "Already Voted", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            new VoteDialog(parent, selectedElection);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DashboardPanel.this, 
                            "Error checking vote status: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            checkWorker.execute();
        }
    }
}
