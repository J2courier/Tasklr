package tasklr.authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginPanel {
    private static final int FIELD_WIDTH = 500;
    private static final int FIELD_HEIGHT = 40;
    private static final int BUTTON_HEIGHT = 40;
    private static final Color BUTTON_COLOR = new Color(0x2E5AEA);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String LOGO_PATH = "C://Users//ADMIN//Desktop//Tasklr//resource//icons//logo1.png";
    private static final Dimension PANEL_SIZE = new Dimension(700, 700);
    
    private final JPanel loginPanel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton signupButton;
    
    public LoginPanel() {
        loginPanel = createMainPanel();
        usernameField = createTextField();
        passwordField = createPasswordField();
        loginButton = createButton("Login");
        signupButton = createButton("Sign Up");
        
        setupLayout();
        setupKeyListeners();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(PANEL_SIZE);
        panel.setBackground(null);
        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(50);
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(50);
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(TEXT_COLOR);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(0, BUTTON_HEIGHT));
        button.setBackground(BUTTON_COLOR);
        return button;
    }

    private void setupLayout() {
        GridBagConstraints gbc = createGridBagConstraints();

        // Add logo
        addLogo(gbc);

        // Add username components
        addUsernameComponents(gbc);

        // Add password components
        addPasswordComponents(gbc);

        // Add buttons
        addButtons(gbc);
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addLogo(GridBagConstraints gbc) {
        ImageIcon compLogo = new ImageIcon(LOGO_PATH);
        JLabel logoLabel = new JLabel(compLogo);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(logoLabel, gbc);
    }

    private void addUsernameComponents(GridBagConstraints gbc) {
        JLabel usernameLabel = createLabel("Username");
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridy = 2;
        loginPanel.add(usernameField, gbc);
    }

    private void addPasswordComponents(GridBagConstraints gbc) {
        JLabel passwordLabel = createLabel("Password");
        gbc.gridy = 3;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy = 4;
        loginPanel.add(passwordField, gbc);
    }

    private void addButtons(GridBagConstraints gbc) {
        gbc.gridy = 5;
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 6;
        loginPanel.add(signupButton, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);
        return label;
    }

    private void setupKeyListeners() {
        KeyAdapter enterKeyListener = createEnterKeyListener();
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }

    private KeyAdapter createEnterKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        };
    }

    // Public methods - these remain unchanged to maintain compatibility
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
