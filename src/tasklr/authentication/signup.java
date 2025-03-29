
package tasklr.authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import tasklr.main.ui.frames.Tasklr;
import tasklr.utilities.DatabaseManager;

public class Signup extends JFrame {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 760;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 700;
    private static final int PANEL_SIZE = 700;
    private static final int FIELD_WIDTH = 500;
    private static final int FIELD_HEIGHT = 40;
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    private static final Color BUTTON_BASE_COLOR = new Color(0x3B82F6);    // New base color
    private static final Color BUTTON_HOVER_COLOR = new Color(0x60A5FA);   // Hover color
    private static final Color BUTTON_PRESSED_COLOR = new Color(0x2563EB); // Pressed color
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String APP_ICON_PATH = "C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png";
    private static final String LOGO_PATH = "C://Users//ADMIN//Desktop//Tasklr//resource//icons//logo1.png";

    private final JTextField createUsernameField;
    private final JPasswordField createPasswordField;
    private final JPasswordField confirmPasswordField;
    private final JCheckBox showPasswordCheckBox;    // Single checkbox for both fields
    private final JButton signupButton;
    private final JPanel signupPanel;

    public Signup() {
        createUsernameField = createTextField();
        createPasswordField = createPasswordField();
        confirmPasswordField = createPasswordField();
        showPasswordCheckBox = createShowPasswordCheckBox();  // Create single checkbox
        signupButton = createButton("Sign Up");
        signupPanel = createSignupPanel();

        initializeFrame();
        setupComponents();
        setupListeners();
    }

    private void initializeFrame() {
        pack();
        setTitle("Sign up");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ImageIcon originalIcon = new ImageIcon(APP_ICON_PATH);
        Image scaledImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(scaledImage);
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        panel.setBackground(BACKGROUND_COLOR);
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
        button.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
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

    private JCheckBox createShowPasswordCheckBox() {
        JCheckBox checkBox = new JCheckBox("Show Passwords");
        checkBox.setFocusable(false);
        checkBox.setForeground(Color.BLACK);
        checkBox.setOpaque(true);
        checkBox.addActionListener(e -> {
            char echoChar = checkBox.isSelected() ? (char) 0 : '•';
            createPasswordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });
        return checkBox;
    }

    private void setupComponents() {
        GridBagConstraints gbc = createGridBagConstraints();

        // Add company logo
        ImageIcon logo = new ImageIcon(LOGO_PATH);
        JLabel logoLabel = new JLabel(logo);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 40, 10); // Add padding around the logo
        signupPanel.add(logoLabel, gbc);
        
        // Reset insets for other components
        gbc.insets = new Insets(5, 10, 10, 10);

        // Username components
        addLabelAndField(gbc, "Create Username", createUsernameField, 1);

        // Password components
        addLabelAndField(gbc, "Create Password", createPasswordField, 3);
        
        // Confirm password components
        gbc.gridy = 5;
        addLabelAndField(gbc, "Confirm Password", confirmPasswordField, 5);
        
        // Add single show password checkbox
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        signupPanel.add(showPasswordCheckBox, gbc);
        
        // Reset constraints
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Signup button
        gbc.gridy = 8;
        signupPanel.add(signupButton, gbc);

        // Login section
        setupLoginSection(gbc);

        // Add signup panel to frame
        addPanelToFrame();
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        return gbc;
    }

    private void addLabelAndField(GridBagConstraints gbc, String labelText, JComponent field, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.BLACK);
        gbc.gridy = gridy;
        signupPanel.add(label, gbc);

        gbc.gridy = gridy + 1;
        signupPanel.add(field, gbc);
    }

    private void setupLoginSection(GridBagConstraints gbc) {
        JLabel loginLabel = new JLabel("Already have an account?");
        loginLabel.setForeground(new Color(0x275CE2));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Changes cursor to hand when hovering
        
        // Add mouse listener to handle click events
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login().setVisible(true);
                dispose();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                // Optional: Add underline when hovering
                loginLabel.setText("<html><u>Already have an account?</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Remove underline when not hovering
                loginLabel.setText("Already have an account?");
            }
        });

        gbc.gridy = 10;
        gbc.gridwidth = 2; // Make it span both columns
        gbc.fill = GridBagConstraints.NONE; // Don't stretch the label
        gbc.anchor = GridBagConstraints.CENTER; // Center the label
        signupPanel.add(loginLabel, gbc);
    }

    private void addPanelToFrame() {
        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.anchor = GridBagConstraints.CENTER;
        add(signupPanel, frameGbc);
    }

    private void setupListeners() {
        setupKeyListeners();
        setupSignupButtonListener();
    }

    private void setupKeyListeners() {
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    signupButton.doClick();
                }
            }
        };

        createUsernameField.addKeyListener(enterKeyListener);
        createPasswordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
    }

    private void setupSignupButtonListener() {
        signupButton.addActionListener(e -> handleSignup());
    }

    private boolean validateFields() {
        String username = createUsernameField.getText().trim();
        String password = new String(createPasswordField.getPassword()).trim();
        String confirmPass = new String(confirmPasswordField.getPassword()).trim();
        
        if (username.isEmpty() && password.isEmpty() && confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in all required fields!",
                "Empty Fields",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please create a username!",
                "Username Required",
                JOptionPane.WARNING_MESSAGE
            );
            createUsernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please create a password!",
                "Password Required",
                JOptionPane.WARNING_MESSAGE
            );
            createPasswordField.requestFocus();
            return false;
        }
        
        if (confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please confirm your password!",
                "Confirmation Required",
                JOptionPane.WARNING_MESSAGE
            );
            confirmPasswordField.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match!",
                "Password Mismatch",
                JOptionPane.WARNING_MESSAGE
            );
            confirmPasswordField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void handleSignup() {
        if (!validateFields()) {
            return;
        }

        String username = createUsernameField.getText().trim();
        String password = new String(createPasswordField.getPassword()).trim();
        
        String hashedPassword = hashPassword(password);
        if (insertUser(username, hashedPassword)) {
            clearFields();
            new Tasklr(username).setVisible(true);
            dispose();
        }
    }

    private void clearFields() {
        createUsernameField.setText("");
        createPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean insertUser(String username, String hashedPassword) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                int userId = insertUserRecord(conn, username, hashedPassword);
                if (userId != -1) {
                    String sessionToken = insertSessionRecord(conn, userId);
                    conn.commit();
                    
                    UserSession.createSession(userId, username, sessionToken);
                    JOptionPane.showMessageDialog(this, "Registered Successfully!");
                    return true;
                }
            } catch (SQLException ex) {
                conn.rollback();
                // Check for duplicate entry error
                if (ex.getErrorCode() == 1062) { // MySQL duplicate entry error code
                    JOptionPane.showMessageDialog(this,
                        "Account Already Existing!",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    throw ex;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Check for connection error
            if (ex.getErrorCode() == 0) { // Connection error
                JOptionPane.showMessageDialog(this,
                    "Database Connection Failed.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error inserting user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    private int insertUserRecord(Connection conn, String username, String hashedPassword) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }
    
    private String insertSessionRecord(Connection conn, int userId) throws SQLException {
        String sessionToken = java.util.UUID.randomUUID().toString();
        String sessionQuery = "INSERT INTO sessions (user_id, session_token) VALUES (?, ?)";
        
        try (PreparedStatement sessionStmt = conn.prepareStatement(sessionQuery)) {
            sessionStmt.setInt(1, userId);
            sessionStmt.setString(2, sessionToken);
            sessionStmt.executeUpdate();
            return sessionToken;
        }
    }
}
