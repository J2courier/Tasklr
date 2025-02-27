package tasklr.main.overveiw;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import tasklr.createPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class totalexpense {
    private static int Ctr = 0; // Make Ctr static
    private static JLabel CtrLbl; // Make CtrLbl static

    public static JPanel createTotalExpensePanel(){
        JPanel panel = createPanel.panel( null, new BorderLayout(), new Dimension(100, 300));
        Border panel_border = BorderFactory.createLineBorder(new Color(0xB9B9B9), 1);
        panel.setBorder(panel_border);

        JPanel CtrPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 170));
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xB9B9B9));
        CtrPanel.setBorder(border);

        JPanel LblPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 50));
  
        CtrLbl = new JLabel("" + Ctr, SwingConstants.CENTER); // Initialize CtrLbl
        CtrLbl.setForeground(new Color(0x414141));
        CtrLbl.setFont(new Font("Arial", Font.BOLD, 50));
        CtrPanel.add(CtrLbl, BorderLayout.CENTER);
        

        JLabel Lbl = new JLabel("TOTAL EXPENSES", SwingConstants.CENTER);
        Lbl.setForeground(Color.BLACK);
        Lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        Lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new PopUpFrame(Lbl.getText()).setVisible(true);
            }
        });
        LblPanel.add(Lbl, BorderLayout.CENTER);

        // Add the components
        panel.add(CtrPanel, BorderLayout.CENTER);
        panel.add(LblPanel, BorderLayout.SOUTH);


        return panel;
    }

    public static void incrementCounter() {
        Ctr++;
        CtrLbl.setText("" + Ctr); // Update the label text
    }
}
