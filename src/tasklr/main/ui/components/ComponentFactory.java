package tasklr.main.ui.components;

import javax.swing.*;
import java.awt.*;

public class ComponentFactory {
    public static JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
    
    public static JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(255); // (80 + 5) * 3
        return scrollPane;
    }
    
    public static JPanel createContainerPanel(String title, int width) {
        // If no specific width is provided, use 600 as default
        width = (width <= 0) ? 600 : width;
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(width, 0));
        mainPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(0x749AAD)));
        
        JPanel titlePanel = createTitlePanel(title);
        JScrollPane scrollPane = createScrollPane();
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private static JPanel createTitlePanel(String title) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));  // Changed from BaseComponent.TITLE_FONT
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        return titlePanel;
    }
}
