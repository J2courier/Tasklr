package tasklr;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.*;
public class expenselist {
    public static JPanel createExpenseList() {
        JPanel panel = createPanel.panel(new Color(0x292E34), null, new Dimension(400, 0));
        Border border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        panel.setBorder(border);
        return panel;
    }
}
