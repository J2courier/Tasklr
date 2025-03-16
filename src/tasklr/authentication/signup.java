
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
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 920;
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 1000;
    private static final int PANEL_SIZE = 700;
    private static final int FIELD_WIDTH = 500;
    private static final int FIELD_HEIGHT = 40;
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    private static final Color BUTTON_BASE_COLOR = new Color(0x3B82F6);    // New base color
    private static final Color BUTTON_HOVER_COLOR = new Color(0x60A5FA);   // Hover color
    private static final Color BUTTON_PRESSED_COLOR = new Color(0x2563EB); // Pressed color
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String APP_ICON_PATH = "C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png";

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
        setIconImage(new ImageIcon(APP_ICON_PATH).getImage());
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

        // Username components
        addLabelAndField(gbc, "Create Username", createUsernameField, 0);

        // Password components
        addLabelAndField(gbc, "Create Password", createPasswordField, 2);
        
        // Confirm password components
        gbc.gridy = 4;
        addLabelAndField(gbc, "Confirm Password", confirmPasswordField, 4);
        
        // Add single show password checkbox
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        signupPanel.add(showPasswordCheckBox, gbc);
        
        // Reset constraints
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Signup button
        gbc.gridy = 7;
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
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        signupPanel.add(loginLabel, gbc);

        JButton loginButton = createButton("Login");  // Using the new createButton method
        gbc.gridx = 1;
        signupPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
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

    private void handleSignup() {
        String username = createUsernameField.getText();
        String password = createPasswordField.getText();
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (password.equals(confirmPassword)) {
            String hashedPassword = hashPassword(password);
            if (insertUser(username, hashedPassword)) {
                clearFields();
                new Tasklr(username).setVisible(true);
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
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