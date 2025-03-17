package tasklr.utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Toast extends JFrame {
    private static final int DISPLAY_TIME = 2000; // 2 seconds
    private static final float OPACITY = 0.9f;
    private static final int BORDER_RADIUS = 5;
    private static final Color SUCCESS_COLOR = new Color(34, 139, 34);
    private static final Color ERROR_COLOR = new Color(178, 34, 34);
    private static final Color INFO_COLOR = new Color(0, 0, 139);

    public enum Type {
        SUCCESS, ERROR, INFO
    }

    public Toast(String message, Type type) {
        setUndecorated(true);
        setOpacity(OPACITY);
        setBackground(new Color(34, 139, 34, 230)); // Solid green with slight transparency
        
        // Create main panel with rounded corners
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackgroundColor(type));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), BORDER_RADIUS * 2, BORDER_RADIUS * 2));
                g2.dispose();
            }
        };
        
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Create message label
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
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
        
        new Timer(DISPLAY_TIME, e -> {
            dispose();
        }).start();
    }

    private Color getBackgroundColor(Type type) {
        switch (type) {
            case SUCCESS:
                return SUCCESS_COLOR;
            case ERROR:
                return ERROR_COLOR;
            case INFO:
                return INFO_COLOR;
            default:
                return INFO_COLOR;
        }
    }

    // Static convenience methods
    public static void success(String message) {
        SwingUtilities.invokeLater(() -> new Toast(message, Type.SUCCESS));
    }

    public static void error(String message) {
        SwingUtilities.invokeLater(() -> new Toast(message, Type.ERROR));
    }

    public static void info(String message) {
        SwingUtilities.invokeLater(() -> new Toast(message, Type.INFO));
    }
}