package tasklr.main.overveiw;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import tasklr.createPanel;

public class paid {
    public static JPanel createPaidPanel(){
        JPanel panel = createPanel.panel( null, new BorderLayout(), new Dimension(100, 300));
        Border panel_border = BorderFactory.createLineBorder(new Color(0x0A4A7E), 1);
        panel.setBorder(panel_border);

        JPanel CtrPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 170));
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x0A4A7E));
        CtrPanel.setBorder(border);

        JPanel LblPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 50));
  
        int doneCtr = 0;
        JLabel CtrLbl = new JLabel("" + doneCtr, SwingConstants.CENTER);
        CtrLbl.setForeground(new Color(0x414141));
        CtrLbl.setFont(new Font("Arial", Font.BOLD, 50));
        CtrPanel.add(CtrLbl, BorderLayout.CENTER);
        

        JLabel Lbl = new JLabel("PAID", SwingConstants.CENTER);
        Lbl.setForeground(Color.BLACK);
        Lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        LblPanel.add(Lbl, BorderLayout.CENTER);

        // Add the components
        panel.add(CtrPanel, BorderLayout.CENTER);
        panel.add(LblPanel, BorderLayout.SOUTH);


        return panel;
    }
}
