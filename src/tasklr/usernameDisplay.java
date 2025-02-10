package tasklr;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
public class usernameDisplay {
    
    public JPanel DisplayUsername(String username) {
        JPanel panel = createPanel.panel(Color.WHITE, new BorderLayout(), new Dimension(0, 100));
        Border border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);

        JLabel usernameContainer = new JLabel(username);

        panel.add(usernameContainer, BorderLayout.EAST);
        panel.setBorder(border);
        return panel;
    }
}
