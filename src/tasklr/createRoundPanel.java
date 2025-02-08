/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tasklr;

/**
 *
 * @author ADMIN
 */
import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
public class createRoundPanel {
        public static JPanel panel(Color color, LayoutManager layout, Dimension dimension, int radius) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
            }
        };
        panel.setPreferredSize(dimension);
        panel.setOpaque(false);
        Border roundedBorder = new LineBorder(Color.BLACK, 1, true);
        panel.setBorder(roundedBorder);
        return panel;
    }
}