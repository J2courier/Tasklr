package tasklr;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverButtonEffect {
    public HoverButtonEffect(JButton button, Color defaultBg, Color hoverBg, Color defaultText, Color hoverText) {
        button.setBackground(defaultBg);
        button.setForeground(defaultText);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
                button.setForeground(hoverText);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultBg);
                button.setForeground(defaultText);
            }
        });
    }
}
