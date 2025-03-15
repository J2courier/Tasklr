package tasklr.authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
    private static final Color BUTTON_COLOR = new Color(0x2E5AEA);
    private static final String APP_ICON_PATH = "C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png";

    private final JTextField createUsernameField;
    private final JTextField createPasswordField;
    private final JPasswordField confirmPasswordField;
    private final JButton signupButton;
    private final JPanel signupPanel;

    public Signup() {
        createUsernameField = createTextField();
        createPasswordField = createTextField();
        confirmPasswordField = createPasswordField();
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
        button.setFocusable(false);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        button.setBackground(BUTTON_COLOR);
        return button;
    }

    private void setupComponents() {
        GridBagConstraints gbc = createGridBagConstraints();

        // Username components
        addLabelAndField(gbc, "Create Username", createUsernameField, 0);

        // Password components
        addLabelAndField(gbc, "Create Password", createPasswordField, 2);

        // Confirm password components
        addLabelAndField(gbc, "Confirm Password", confirmPasswordField, 4);

        // Signup button
        gbc.gridy = 6;
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
        loginLabel.setForeground(Color.BLACK);
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        signupPanel.add(loginLabel, gbc);

        JButton loginButton = createButton("Login");
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
                throw ex;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting user: " + ex.getMessage());
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