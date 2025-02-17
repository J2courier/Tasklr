package tasklr.main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import tasklr.createPanel;

public class done {
    public static JPanel createDonePanel() {
        JPanel done_panel = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(100, 200));
        Border done_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        done_panel.setBorder(done_border);

        JPanel doneCtrPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 170));
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        doneCtrPanel.setBorder(border);

        JPanel doneLblPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 30));
        // Border border1 = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        // doneLblPanel.setBorder(border1);
        // Default counter starts with 0
        int doneCtr = 0;
        JLabel doneCtrLbl = new JLabel("" + doneCtr, SwingConstants.CENTER);
        doneCtrLbl.setForeground(Color.WHITE);
        doneCtrLbl.setFont(new Font("Arial", Font.BOLD, 50));
        doneCtrPanel.add(doneCtrLbl, BorderLayout.CENTER);
        

        JLabel doneLbl = new JLabel("COMPLETED", SwingConstants.CENTER);
        doneLbl.setForeground(Color.WHITE);
        doneLbl.setFont(new Font("Arial", Font.PLAIN, 16));
        doneLblPanel.add(doneLbl, BorderLayout.CENTER);

        // Add the components
        done_panel.add(doneCtrPanel, BorderLayout.CENTER);
        done_panel.add(doneLblPanel, BorderLayout.SOUTH);
    

        return done_panel;
    }
}
