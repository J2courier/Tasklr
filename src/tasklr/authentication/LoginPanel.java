package tasklr.authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel {
    private static final int FIELD_WIDTH = 500;
    private static final int FIELD_HEIGHT = 40;
    private static final int BUTTON_HEIGHT = 40;
    private static final Color BUTTON_BASE_COLOR = new Color(0x3B82F6);    // New base color
    private static final Color BUTTON_HOVER_COLOR = new Color(0x60A5FA);   // Hover color
    private static final Color BUTTON_PRESSED_COLOR = new Color(0x2563EB); // Pressed color
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String LOGO_PATH = "C://Users//ADMIN//Desktop//Tasklr//resource//icons//logo1.png";
    private static final Dimension PANEL_SIZE = new Dimension(700, 700);
    private static final Color LINK_COLOR = new Color(0x275CE2); // Add this constant for consistency
    
    private final JPanel loginPanel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JCheckBox showPasswordCheckBox; // Add this field
    private final JLabel forgotPasswordLabel; // Add this field
    
    public LoginPanel() {
        loginPanel = createMainPanel();
        usernameField = createTextField();
        passwordField = createPasswordField();
        loginButton = createButton("Login");
        showPasswordCheckBox = createShowPasswordCheckBox();
        forgotPasswordLabel = createForgotPasswordLabel(); // Initialize the label
        
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
        button.setBackground(BUTTON_BASE_COLOR);
        
        // Add hover and pressed effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BASE_COLOR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(BUTTON_PRESSED_COLOR);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    button.setBackground(BUTTON_HOVER_COLOR);
                } else {
                    button.setBackground(BUTTON_BASE_COLOR);
                }
            }
        });

        // Remove button borders and focus painting
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

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
        ImageIcon logo = new ImageIcon(LOGO_PATH);
        JLabel logoLabel = new JLabel(logo);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 40, 10); // Add padding around the logo
        loginPanel.add(logoLabel, gbc);
        gbc.insets = new Insets(5, 10, 10, 10); // Reset insets for other components
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

        // Create a panel for checkbox and forgot password
        JPanel passwordOptionsPanel = new JPanel(new GridBagLayout());
        passwordOptionsPanel.setBackground(null);
        
        GridBagConstraints optionsGbc = new GridBagConstraints();
        optionsGbc.gridx = 0;
        optionsGbc.gridy = 0;
        optionsGbc.anchor = GridBagConstraints.WEST;
        optionsGbc.weightx = 1.0; // Give weight to create space between components
        passwordOptionsPanel.add(showPasswordCheckBox, optionsGbc);
        
        optionsGbc.gridx = 1;
        optionsGbc.anchor = GridBagConstraints.EAST;
        optionsGbc.weightx = 0.0; // Reset weight for the forgot password label
        passwordOptionsPanel.add(forgotPasswordLabel, optionsGbc);

        // Add the options panel
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(passwordOptionsPanel, gbc);
        
        // Reset constraints for subsequent components
        gbc.anchor = GridBagConstraints.CENTER;
    }

    private void addButtons(GridBagConstraints gbc) {
        gbc.gridy = 6; // Increment gridy by 1 for login button
        loginPanel.add(loginButton, gbc);

        // Add signup label
        JLabel signupLabel = createSignupLabel();
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE; // Don't stretch the label
        gbc.anchor = GridBagConstraints.CENTER; // Center the label
        loginPanel.add(signupLabel, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JLabel createSignupLabel() {
        JLabel signupLabel = new JLabel("New user sign up here!");
        signupLabel.setForeground(new Color(0x275CE2)); // Blue color
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Window window = SwingUtilities.getWindowAncestor(loginPanel);
                if (window != null) {
                    window.dispose();
                    new Signup().setVisible(true);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                signupLabel.setText("<html><u>New user sign up here!</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signupLabel.setText("New user sign up here!");
            }
        });
        
        return signupLabel;
    }

    private JLabel createForgotPasswordLabel() {
        JLabel label = new JLabel("Forgot password?");
        label.setForeground(LINK_COLOR);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setText("<html><u>Forgot password?</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                label.setText("Forgot password?");
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO: Implement forgot password functionality
                JOptionPane.showMessageDialog(loginPanel, 
                    "Forgot password functionality will be implemented soon.",
                    "Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
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

    // Add this method to create the checkbox
    private JCheckBox createShowPasswordCheckBox() {
        JCheckBox checkBox = new JCheckBox("Show Password");
        checkBox.setFocusable(false);
        checkBox.setBackground(null);
        checkBox.setForeground(Color.BLACK);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show password
            } else {
                passwordField.setEchoChar('•'); // Hide password
            }
        });
        return checkBox;
    }

    public boolean validateFields() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty() && password.isEmpty()) {
            JOptionPane.showMessageDialog(
                loginPanel,
                "Please fill in both username and password fields!",
                "Empty Fields",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(
                loginPanel,
                "Please enter your username!",
                "Username Required",
                JOptionPane.WARNING_MESSAGE
            );
            usernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(
                loginPanel,
                "Please enter your password!",
                "Password Required",
                JOptionPane.WARNING_MESSAGE
            );
            passwordField.requestFocus();
            return false;
        }
        
        return true;
    }
}
