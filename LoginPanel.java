import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public LoginPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Go to Register");
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
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(loginButton, gbc);

        gbc.gridy = 3;
        add(registerButton, gbc);

        gbc.gridy = 4;
        add(messageLabel, gbc);
    }

    private void setupListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "REGISTER");
            }
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        try {
            JSONObject loginData = new JSONObject();
            loginData.put("username", username);
            loginData.put("password", password);

            HttpResponse<String> response = ApiClient.post("/auth/login", loginData.toString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                try {
                    JSONObject loginResponse = new JSONObject(responseBody);
                    String token = loginResponse.getString("token");
                    ApiClient.setJwtToken(token);
                    // Check if admin
                    if (username.equals("admin")) {
                        cardLayout.show(mainPanel, "ADMIN");
                    } else {
                        cardLayout.show(mainPanel, "DASHBOARD");
                    }
                    messageLabel.setText("");
                } catch (Exception jsonEx) {
                    // Assume the response body is the token
                    ApiClient.setJwtToken(responseBody.trim());
                    // Assume admin for this app
                    cardLayout.show(mainPanel, "ADMIN");
                    messageLabel.setText("");
                }
            } else {
                try {
                    JSONObject errorResponse = new JSONObject(response.body());
                    messageLabel.setText(errorResponse.optString("message", "Login failed"));
                } catch (Exception jsonEx) {
                    messageLabel.setText("Login failed: " + response.body());
                }
            }
        } catch (Exception ex) {
            messageLabel.setText("Connection error: " + ex.getMessage());
        }
    }
}
