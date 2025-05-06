package tasklr.authentication;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import tasklr.main.ui.frames.Tasklr;
import tasklr.utilities.DatabaseManager;

public class Login extends JFrame {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 760;
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 700;
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    private static final String APP_ICON_PATH = "C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png";
    private final JPanel centerContainer;

    public Login() {
        initializeFrame();
        centerContainer = createCenterContainer();
        initializeLoginPanel();
    }

    private void initializeFrame() {
        pack();
        setTitle("Login");
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

    private JPanel createCenterContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_COLOR);
        return container;
    }

    private void initializeLoginPanel() {
        LoginPanel loginPanel = new LoginPanel();
        setupLoginListener(loginPanel);
        addLoginPanelToFrame(loginPanel);
    }

    private void setupLoginListener(LoginPanel loginPanel) {
        loginPanel.addLoginListener(e -> {
            if (!loginPanel.validateFields()) {
                return;
            }
            
            String username = loginPanel.getUsername();
            String password = loginPanel.getPassword();
            String hashedPassword = hashPassword(password);
            int userId = validateUser(username, hashedPassword);
            if (userId != -1) {
                handleSuccessfulLogin(userId, username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        });
    }

    private void addLoginPanelToFrame(LoginPanel loginPanel) {
        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.anchor = GridBagConstraints.CENTER;
        add(loginPanel.getLoginPanel(), frameGbc);
    }

    private void handleSuccessfulLogin(int userId, String username) {
        String sessionToken = generateSessionToken();
        UserSession.createSession(userId, username, sessionToken);
        new Tasklr(username).setVisible(true);
        dispose();
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

    private int validateUser(String username, String hashedPassword) {
        String query = "SELECT id, username FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error validating user: " + ex.getMessage());
        }
        return -1;
    }

    private String generateSessionToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
