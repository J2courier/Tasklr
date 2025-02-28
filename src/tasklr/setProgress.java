package tasklr;

import javax.swing.*;
import java.awt.*;
import tasklr.main.overveiw.ongoing;
import tasklr.main.overveiw.done;

public class setProgress extends JFrame {
    private static boolean isOngoingSet = false;

    public setProgress(){
        setTitle("Set Progress");
        setSize(400, 100);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(0xf1f3f6));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 0));
        JButton updateBtn = createButton.button("Update", null, new Color(0x0A4A7E), null, false);
        JButton deleteBtn = createButton.button("Delete", null, new Color(0x0A4A7E), null, false);

        JButton completeBtn = createButton.button("Complete", null, new Color(0x0A4A7E), null, false);
        completeBtn.setPreferredSize(new Dimension(200, 40));
        
        panel.add(completeBtn, BorderLayout.EAST);
        add(panel, BorderLayout.CENTER);
    }

  

}
