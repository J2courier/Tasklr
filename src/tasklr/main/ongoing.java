package tasklr.main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import tasklr.createPanel;

public class ongoing {
    
    public static JPanel createOngoingPanel() {
        JPanel ongoing_panel = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(100, 200));
        Border ongoing_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        ongoing_panel.setBorder(ongoing_border);

        JPanel ongoingCtrPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 170));
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        ongoingCtrPanel.setBorder(border);

        JPanel ongoingLblPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 30));
        // Border border1 = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        // ongoingLblPanel.setBorder(border1);
        // Default counter starts with 0
        int ongoingCtr = 0;
        JLabel ongoingCtrLbl = new JLabel("" + ongoingCtr, SwingConstants.CENTER);
        ongoingCtrLbl.setForeground(Color.WHITE);
        ongoingCtrLbl.setFont(new Font("Arial", Font.BOLD, 50));
        ongoingCtrPanel.add(ongoingCtrLbl, BorderLayout.CENTER);
        

        JLabel ongoingLbl = new JLabel("ONGOING TASK", SwingConstants.CENTER);
        ongoingLbl.setForeground(Color.WHITE);
        ongoingLbl.setFont(new Font("Arial", Font.PLAIN, 16));
        ongoingLblPanel.add(ongoingLbl, BorderLayout.CENTER);

        // Add the components
        ongoing_panel.add(ongoingCtrPanel, BorderLayout.CENTER);
        ongoing_panel.add(ongoingLblPanel, BorderLayout.SOUTH);
    

        return ongoing_panel;
    }
}