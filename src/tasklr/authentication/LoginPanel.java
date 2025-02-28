package tasklr.authentication;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPanel {
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        // Border loginBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        // loginPanel.setBorder(loginBorder);
        loginPanel.setPreferredSize(new Dimension(700, 700));
        loginPanel.setBackground(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ImageIcon CompLogo = new ImageIcon("C://Users//ADMIN//Desktop//Tasklr//resource//icons//logo1.png");
        JLabel logoLabel = new JLabel(CompLogo);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(logoLabel, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.BLACK);
        
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        loginPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(50);
        usernameField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 2;
        loginPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.BLACK);
        gbc.gridy = 3;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(50);
        passwordField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 4;
        loginPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusable(false);
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setBackground(new Color(0x2E5AEA));
        gbc.gridy = 5;
        loginPanel.add(loginButton, gbc);

        signupButton = new JButton("Sign Up");
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusable(false);
        signupButton.setPreferredSize(new Dimension(0, 40));
        signupButton.setBackground(new Color(0x2E5AEA));
        gbc.gridy = 6;
        loginPanel.add(signupButton, gbc);

        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        };

        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
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
