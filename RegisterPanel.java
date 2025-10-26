import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class RegisterPanel extends JPanel {
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField voterIdField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public RegisterPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        fullNameField = new JTextField(20);
        voterIdField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Voter ID:"), gbc);

        gbc.gridx = 1;
        add(voterIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(registerButton, gbc);

        gbc.gridy = 7;
        add(backButton, gbc);

        gbc.gridy = 8;
        add(messageLabel, gbc);
    }

    private void setupListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        });
    }

    private void register() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String voterId = voterIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || voterId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!isValidEmail(email)) {
            messageLabel.setText("Please enter a valid email address");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        try {
            JSONObject registerData = new JSONObject();
            registerData.put("username", username);
            registerData.put("email", email);
            registerData.put("fullName", fullName);
            registerData.put("voterId", voterId);
            registerData.put("password", password);

            HttpResponse<String> response = ApiClient.post("/auth/register", registerData.toString());

            if (response.statusCode() == 200) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Registration successful! Please login.");
                clearFields();
            } else {
                // Handle non-JSON responses (e.g., HTML error pages)
                String body = response.body().trim();
                if (body.startsWith("{")) {
                    try {
                        JSONObject errorResponse = new JSONObject(body);
                        messageLabel.setText(errorResponse.optString("message", "Registration failed"));
                    } catch (Exception e) {
                        messageLabel.setText("Registration failed: " + body);
                    }
                } else {
                    messageLabel.setText("Registration failed: " + body);
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        usernameField.setText("");
        emailField.setText("");
        fullNameField.setText("");
        voterIdField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
