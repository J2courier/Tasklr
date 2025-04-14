package tasklr.main.ui.components;

import javax.swing.*;
import java.awt.*;

public class TaskCounterPanel extends BaseComponent {
    private static final Color BORDER_COLOR = new Color(0x749AAD);
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 100;
    private static final Font TITLE_FONT = new Font("Segoe UI Variable", Font.BOLD, 18); 
    private static final Font COUNTER_FONT = new Font("Segoe UI Variable", Font.BOLD, 24); 
    private final JLabel counterLabel;
    private final String title;
    private int count;
    
    public TaskCounterPanel(int initialCount, String title) {
        super(new BorderLayout());
        this.count = initialCount;
        this.title = title;
        this.counterLabel = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        setupPanel();
    }
    
    private void setupPanel() {
        setupSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setupBorder(BORDER_COLOR);
        setupLabels();
    }
    
    private void setupLabels() {
        // Title label
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        
        // Counter label
        counterLabel.setFont(COUNTER_FONT);
        
        // Add labels to panel
        add(titleLabel, BorderLayout.NORTH);
        add(counterLabel, BorderLayout.CENTER);
    }
    
    public void updateCount(int newCount) {
        this.count = newCount;
        SwingUtilities.invokeLater(() -> 
            counterLabel.setText(String.valueOf(newCount))
        );
    }
    
    public int getCount() {
        return count;
    }
    
    public JPanel createPanel() {
        return this;
    }
}
