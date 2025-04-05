package tasklr.utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Toast extends JDialog {
    private static final int DISPLAY_TIME = 3000; // 3 seconds default
    private static final float OPACITY = 0.9f;
    private static final int BORDER_RADIUS = 10;
    private static final Color SUCCESS_COLOR = new Color(34, 139, 34);
    private static final Color ERROR_COLOR = new Color(178, 34, 34);
    private static final Color INFO_COLOR = new Color(0, 0, 139);
    private static final Color WARNING_COLOR = new Color(255, 140, 0);

    public enum Type {
        SUCCESS, ERROR, INFO, WARNING
    }

    // Original constructor for backward compatibility
    public Toast(String message, Type type) {
        this(message, getBackgroundColor(type), Color.WHITE, DISPLAY_TIME);
    }

    // New constructor for custom styling
    private Toast(String message, Color backgroundColor, Color textColor, int displayTime) {
        setUndecorated(true);
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        setBackground(new Color(0, 0, 0, 0));
        
        // Create main panel with custom background
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS * 2, BORDER_RADIUS * 2);
                g2.dispose();
            }
        };
        
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Create message label with custom text color
        JLabel label = new JLabel(message);
        label.setForeground(textColor);
        label.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        panel.add(label, BorderLayout.CENTER);
        
        add(panel);
        pack();
        
        // Center on screen
        setLocationRelativeTo(null);
        setLocation(getX(), 50);
    
        // Set shape with rounded corners
        setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), BORDER_RADIUS * 2, BORDER_RADIUS * 2));
        
        // Show toast and set timer to close it
        setVisible(true);
        
        new Timer(displayTime, e -> {
            dispose();
        }).start();
    }

    private static Color getBackgroundColor(Type type) {
        return switch (type) {
            case SUCCESS -> SUCCESS_COLOR;
            case ERROR -> ERROR_COLOR;
            case INFO -> INFO_COLOR;
            case WARNING -> WARNING_COLOR;
        };
    }

    // Static convenience methods for standard toasts
    public static void success(String message) {
        SwingUtilities.invokeLater(() -> new Toast(message, Type.SUCCESS));
    }

    public static void error(String message) {
        SwingUtilities.invokeLater(() -> new Toast(message, Type.ERROR));
    }

    public static void info(String message) {
        SwingUtilities.invokeLater(() -> new Toast(message, Type.INFO));
    }

    public static void warning(String message) {
        SwingUtilities.invokeLater(() -> new Toast(message, Type.WARNING));
    }

    // Custom toast with default display time
    public static void custom(String message, Color backgroundColor, Color textColor) {
        custom(message, backgroundColor, textColor, DISPLAY_TIME);
    }

    // Custom toast with specified display time
    public static void custom(String message, Color backgroundColor, Color textColor, int displayTime) {
        SwingUtilities.invokeLater(() -> new Toast(message, backgroundColor, textColor, displayTime));
    }

    // Method specifically for task reminders
    public static void taskReminder(String message, boolean isUrgent) {
        if (isUrgent) {
            custom(message, new Color(0xFF4444), Color.WHITE, 5000); // Red, urgent reminder
        } else {
            custom(message, WARNING_COLOR, Color.WHITE, 4000); // Orange, normal reminder
        }
    }
}

