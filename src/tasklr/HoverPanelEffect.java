package tasklr;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverPanelEffect {
    private final Color defaultColor;
    private final Color hoverColor;
    private final Border defaultBorder;
    private final Border hoverBorder;

    public HoverPanelEffect(JPanel panel, Color defaultColor, Color hoverColor) {
        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.defaultBorder = panel.getBorder(); // Keep the original border
        this.hoverBorder = BorderFactory.createLineBorder(new Color(0x2E5AEA), 2); // Example hover border

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverColor);
                panel.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(defaultColor);
                panel.setBorder(defaultBorder);
            }
        });
    }
}