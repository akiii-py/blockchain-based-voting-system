package com.evoting.swingclient.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.evoting.swingclient.EVotingClient;
import com.evoting.swingclient.model.Election;
import com.evoting.swingclient.model.Candidate;
import com.evoting.swingclient.api.ElectionService;

public class AdminPanel extends JPanel {
    private EVotingClient parent;
    private JTabbedPane tabbedPane;
    
    // Elections tab
    private JTable electionsTable;
    private DefaultTableModel electionsModel;
    private JButton createElectionButton;
    private JButton activateButton;
    private JButton deactivateButton;
    private JButton addCandidateButton;
    private JButton refreshElectionsButton;
    private List<Election> currentElections;
    
    // Candidates tab
    private JComboBox<Election> electionComboBox;
    private JTable candidatesTable;
    private DefaultTableModel candidatesModel;
    private JButton refreshCandidatesButton;
    
    private JButton logoutButton;
    private JLabel statusLabel;
    
    public AdminPanel(EVotingClient parent) {
        this.parent = parent;
        initializeComponents();
        layoutComponents();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // Elections table
        electionsModel = new DefaultTableModel(
            new Object[]{"ID", "Title", "Description", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        electionsTable = new JTable(electionsModel);
        electionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        electionsTable.setRowHeight(25);
        
        createElectionButton = new JButton("Create Election");
        activateButton = new JButton("Activate");
        deactivateButton = new JButton("Deactivate");
        addCandidateButton = new JButton("Add Candidate");
        refreshElectionsButton = new JButton("Refresh");
        
        // Candidates table
        candidatesModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Party", "Description", "Vote Count"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        candidatesTable = new JTable(candidatesModel);
        candidatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidatesTable.setRowHeight(25);
        
        electionComboBox = new JComboBox<>();
        refreshCandidatesButton = new JButton("Refresh");
        
        logoutButton = new JButton("Logout");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        
        // Add action listeners
        createElectionButton.addActionListener(e -> showCreateElectionDialog());
        activateButton.addActionListener(e -> activateSelectedElection());
        deactivateButton.addActionListener(e -> deactivateSelectedElection());
        addCandidateButton.addActionListener(e -> showAddCandidateDialog());
        refreshElectionsButton.addActionListener(e -> refreshElections());
        refreshCandidatesButton.addActionListener(e -> refreshCandidates());
        logoutButton.addActionListener(e -> parent.logout());
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Admin Panel - " + 
            (parent.getCurrentUser() != null ? parent.getCurrentUser().getFullName() : "Administrator"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topButtonPanel.add(refreshElectionsButton);
        topButtonPanel.add(logoutButton);
        topPanel.add(topButtonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Elections tab
        JPanel electionsPanel = new JPanel(new BorderLayout(10, 10));
        electionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane electionsScroll = new JScrollPane(electionsTable);
        electionsPanel.add(electionsScroll, BorderLayout.CENTER);
        
        JPanel electionsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        electionsButtonPanel.add(createElectionButton);
        electionsButtonPanel.add(activateButton);
        electionsButtonPanel.add(deactivateButton);
        electionsButtonPanel.add(addCandidateButton);
        electionsPanel.add(electionsButtonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Elections", electionsPanel);
        
        // Candidates tab
        JPanel candidatesPanel = new JPanel(new BorderLayout(10, 10));
        candidatesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel candidatesTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        candidatesTopPanel.add(new JLabel("Election:"));
        candidatesTopPanel.add(electionComboBox);
        candidatesTopPanel.add(refreshCandidatesButton);
        candidatesPanel.add(candidatesTopPanel, BorderLayout.NORTH);
        
        JScrollPane candidatesScroll = new JScrollPane(candidatesTable);
        candidatesPanel.add(candidatesScroll, BorderLayout.CENTER);
        
        tabbedPane.addTab("Candidates", candidatesPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status label
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    public void refresh() {
        refreshElections();
    }
    
    private void refreshElections() {
        statusLabel.setText("Loading elections...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<List<Election>, Void> worker = new SwingWorker<List<Election>, Void>() {
            @Override
            protected List<Election> doInBackground() throws Exception {
                // Get all elections (active and inactive) for admin panel
                return ElectionService.getAllElections(parent.getAuthToken());
            }
            
            @Override
            protected void done() {
                try {
                    List<Election> elections = get();
                    electionsModel.setRowCount(0);
                    electionComboBox.removeAllItems();
                    
                    if (elections != null && !elections.isEmpty()) {
                        currentElections = elections;
                        for (Election election : elections) {
                            electionsModel.addRow(new Object[]{
                                election.getId(),
                                election.getTitle(),
                                election.getDescription(),
                                election.isActive() ? "Active" : "Inactive"
                            });
                            electionComboBox.addItem(election);
                        }
                        statusLabel.setText("Loaded " + elections.size() + " election(s)");
                        statusLabel.setForeground(Color.GREEN);
                    } else {
                        currentElections = java.util.Collections.emptyList();
                        statusLabel.setText("No elections found");
                        statusLabel.setForeground(Color.ORANGE);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error loading elections: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void refreshCandidates() {
        Election selected = (Election) electionComboBox.getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an election");
            statusLabel.setForeground(Color.ORANGE);
            return;
        }
        
        statusLabel.setText("Loading candidates...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<List<Candidate>, Void> worker = new SwingWorker<List<Candidate>, Void>() {
            @Override
            protected List<Candidate> doInBackground() throws Exception {
                return ElectionService.getCandidates(selected.getId(), parent.getAuthToken());
            }
            
            @Override
            protected void done() {
                try {
                    List<Candidate> candidates = get();
                    candidatesModel.setRowCount(0);
                    
                    if (candidates != null) {
                        for (Candidate candidate : candidates) {
                            candidatesModel.addRow(new Object[]{
                                candidate.getId(),
                                candidate.getName(),
                                candidate.getParty(),
                                candidate.getDescription(),
                                candidate.getVoteCount()
                            });
                        }
                        statusLabel.setText("Loaded " + candidates.size() + " candidate(s)");
                        statusLabel.setForeground(Color.GREEN);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error loading candidates: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }
    
    private void showCreateElectionDialog() {
        new CreateElectionDialog(parent);
        refreshElections();
    }
    
    private void activateSelectedElection() {
        int row = electionsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an election");
            return;
        }
        
        Election election = currentElections.get(row);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ElectionService.activateElection(election.getId(), parent.getAuthToken());
                // Small delay to ensure database update completes
                Thread.sleep(500);
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(AdminPanel.this, "Election activated successfully");
                    // Force immediate refresh to show updated status
                    SwingUtilities.invokeLater(() -> {
                        // Refresh immediately and again after delay for reliability
                        refreshElections();
                        new Timer(1000, ev -> {
                            refreshElections();
                            ((Timer)ev.getSource()).stop();
                        }).start();
                    });
                } catch (Exception e) {
                    String errorMsg = e.getMessage();
                    if (errorMsg == null || errorMsg.isEmpty()) {
                        errorMsg = e.getClass().getSimpleName();
                    }
                    JOptionPane.showMessageDialog(AdminPanel.this, 
                        "Error activating election: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void deactivateSelectedElection() {
        int row = electionsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an election");
            return;
        }
        
        Election election = currentElections.get(row);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ElectionService.deactivateElection(election.getId(), parent.getAuthToken());
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(AdminPanel.this, "Election deactivated successfully");
                    SwingUtilities.invokeLater(() -> {
                        refreshElections();
                        new Timer(1000, ev -> {
                            refreshElections();
                            ((Timer)ev.getSource()).stop();
                        }).start();
                    });
                } catch (Exception e) {
                    String errorMsg = e.getMessage();
                    if (errorMsg == null || errorMsg.isEmpty()) {
                        errorMsg = e.getClass().getSimpleName();
                    }
                    JOptionPane.showMessageDialog(AdminPanel.this, 
                        "Error deactivating election: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void showAddCandidateDialog() {
        int row = electionsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an election first");
            return;
        }
        
        Election election = currentElections.get(row);
        new AddCandidateDialog(parent, election);
        refreshCandidates();
    }
    
    // Inner classes for dialogs
    private class CreateElectionDialog extends JDialog {
        public CreateElectionDialog(EVotingClient parent) {
            super((Frame) SwingUtilities.getWindowAncestor(parent), "Create Election", true);
            
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            JTextField titleField = new JTextField(30);
            JTextArea descArea = new JTextArea(3, 30);
            // time-window removed
            
            int row = 0;
            panel.add(new JLabel("Title:"), gbc); gbc.gridx = 1; panel.add(titleField, gbc);
            gbc.gridx = 0; gbc.gridy = ++row;
            panel.add(new JLabel("Description:"), gbc); gbc.gridx = 1; panel.add(new JScrollPane(descArea), gbc);
            gbc.gridx = 0; gbc.gridy = ++row;
            // no start/end time fields
            
            JButton createButton = new JButton("Create");
            createButton.addActionListener(e -> {
                try {
                    SwingWorker<Election, Void> worker = new SwingWorker<Election, Void>() {
                        @Override
                        protected Election doInBackground() throws Exception {
                            return ElectionService.createElection(
                                titleField.getText(),
                                descArea.getText(),
                                parent.getAuthToken()
                            );
                        }
                        
                        @Override
                        protected void done() {
                            try {
                                get();
                                JOptionPane.showMessageDialog(CreateElectionDialog.this, "Election created successfully");
                                dispose();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(CreateElectionDialog.this, 
                                    "Error: " + ex.getMessage());
                            }
                        }
                    };
                    worker.execute();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });
            
            gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
            panel.add(createButton, gbc);
            
            add(panel);
            pack();
            setLocationRelativeTo(parent);
            setVisible(true);
        }
    }
    
    private class AddCandidateDialog extends JDialog {
        public AddCandidateDialog(EVotingClient parent, Election election) {
            super((Frame) SwingUtilities.getWindowAncestor(parent), "Add Candidate", true);
            
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            JTextField nameField = new JTextField(30);
            JTextField partyField = new JTextField(30);
            JTextArea descArea = new JTextArea(3, 30);
            
            int row = 0;
            panel.add(new JLabel("Name:"), gbc); gbc.gridx = 1; panel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = ++row;
            panel.add(new JLabel("Party:"), gbc); gbc.gridx = 1; panel.add(partyField, gbc);
            gbc.gridx = 0; gbc.gridy = ++row;
            panel.add(new JLabel("Description:"), gbc); gbc.gridx = 1; panel.add(new JScrollPane(descArea), gbc);
            
            JButton addButton = new JButton("Add");
            addButton.addActionListener(e -> {
                SwingWorker<Candidate, Void> worker = new SwingWorker<Candidate, Void>() {
                    @Override
                    protected Candidate doInBackground() throws Exception {
                        return ElectionService.addCandidate(
                            election.getId(),
                            nameField.getText(),
                            partyField.getText(),
                            descArea.getText(),
                            parent.getAuthToken()
                        );
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            get();
                            JOptionPane.showMessageDialog(AddCandidateDialog.this, "Candidate added successfully");
                            dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(AddCandidateDialog.this, 
                                "Error: " + ex.getMessage());
                        }
                    }
                };
                worker.execute();
            });
            
            gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
            panel.add(addButton, gbc);
            
            add(panel);
            pack();
            setLocationRelativeTo(parent);
            setVisible(true);
        }
    }
}
