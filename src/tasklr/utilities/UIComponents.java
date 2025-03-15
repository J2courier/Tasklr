package tasklr.utilities;

import javax.swing.*;
import java.awt.*;

public class UIComponents {
    private static JPanel createTitlePanel(String title) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        return titlePanel;
    }

    public static JPanel createListContainer(String title, int width) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(width, 0));
        mainPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(0x749AAD)));

        JPanel titlePanel = createTitlePanel(title);
        JScrollPane scrollPane = createScrollPane();
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    public static JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement((80 + 5) * 3);
        return scrollPane;
    }
    
    public static JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0x0082FC));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }
}