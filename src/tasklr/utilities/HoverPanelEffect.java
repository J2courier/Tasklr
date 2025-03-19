
package tasklr.utilities;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverPanelEffect {
    private final Color defaultColor;
    private final Color hoverColor;
    private final Border defaultBorder;

    public HoverPanelEffect(JPanel panel, Color defaultColor, Color hoverColor) {
        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE5E7EB), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        panel.setBorder(defaultBorder);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverColor);
                // Update background of all child components
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JPanel) {
                        comp.setBackground(hoverColor);
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(defaultColor);
                // Restore background of all child components
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JPanel) {
                        comp.setBackground(defaultColor);
                    }
                }
            }
        });
    }
}
