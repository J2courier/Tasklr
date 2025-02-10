package tasklr.login;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel {
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        Border loginBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        loginPanel.setBorder(loginBorder);
        loginPanel.setPreferredSize(new Dimension(700, 700));
        loginPanel.setBackground(new Color(0x292E34));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(50);
        usernameField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(50);
        passwordField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 3;
        loginPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFocusable(false);
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setBackground(new Color(0x2E5AEA));
        gbc.gridy = 4;
        loginPanel.add(loginButton, gbc);

        signupButton = new JButton("Sign Up");
        signupButton.setFocusable(false);
        signupButton.setPreferredSize(new Dimension(0, 40));
        signupButton.setBackground(new Color(0x2E5AEA));
        gbc.gridy = 5;
        loginPanel.add(signupButton, gbc);
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addSignupListener(ActionListener listener) {
        signupButton.addActionListener(listener);
    }
}
