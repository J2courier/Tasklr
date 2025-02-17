package tasklr.main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import tasklr.createPanel;
public class unpaid {
    public static JPanel createUnpaidPanel(){
        JPanel panel = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(100, 200));
        Border border1 = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        panel.setBorder(border1);

        JPanel CtrPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 170));
        Border border2 = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        CtrPanel.setBorder(border2);

        JPanel LblPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 30));
        // Border border3 = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        // LblPanel.setBorder(border3);
        // Default counter starts with 0
        int Ctr = 0;
        JLabel CtrLbl = new JLabel("" + Ctr, SwingConstants.CENTER);
        CtrLbl.setForeground(Color.WHITE);
        CtrLbl.setFont(new Font("Arial", Font.BOLD, 50));
        CtrPanel.add(CtrLbl, BorderLayout.CENTER);
        

        JLabel Lbl = new JLabel("UNPAID EXPENSE", SwingConstants.CENTER);
        Lbl.setForeground(Color.WHITE);
        Lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        LblPanel.add(Lbl, BorderLayout.CENTER);

        // Add the components
        panel.add(CtrPanel, BorderLayout.CENTER);
        panel.add(LblPanel, BorderLayout.SOUTH);

        return panel;
    }
}
