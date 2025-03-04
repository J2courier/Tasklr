package tasklr;

import javax.swing.*;
import java.awt.*;

public class ComponentUtil {
    public static void addComponent(JPanel panel, JComponent comp, int x, int y, int width, int height, Insets insets, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = weighty;
        gbc.insets = insets; 
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(comp, gbc);
    }
}
