
package tasklr.utilities;

/**
 *
 * @author ADMIN
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class createHoverEffect {
    public JButton createHoverEffect(JButton button, Color hoverBgColor, Color hoverTextColor) {
        Color originalBgColor = button.getBackground();
        Color originalTextColor = button.getForeground();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBgColor);
                button.setForeground(hoverTextColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBgColor);
                button.setForeground(originalTextColor);
            }
        });

        return button;
    }
}


