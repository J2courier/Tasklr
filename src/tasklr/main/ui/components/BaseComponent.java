package tasklr.main.ui.components;

import javax.swing.*;
import java.awt.*;

public abstract class BaseComponent extends JPanel {
    protected static final Color DEFAULT_BACKGROUND = new Color(0xf1f3f6);
    protected static final Font TITLE_FONT = new Font("Segoe UI Variable", Font.BOLD, 24);
    protected static final Font COUNTER_FONT = new Font("Segoe UI Variable", Font.BOLD, 24);
    
    protected BaseComponent() {
        setBackground(DEFAULT_BACKGROUND);
    }
    
    protected BaseComponent(LayoutManager layout) {
        super(layout);
        setBackground(DEFAULT_BACKGROUND);
    }
    
    protected void setupBorder(Color borderColor) {
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, borderColor));
    }
    
    protected void setupSize(Dimension dimension) {
        setPreferredSize(dimension);
        setMinimumSize(dimension);
    }
}
