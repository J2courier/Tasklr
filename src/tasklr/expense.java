package tasklr;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.*;
public class expense {
    public static JPanel createExpensePanel(){
        JPanel panel = createPanel.panel(new Color(0x1C2128), new BorderLayout(), new Dimension(100, 100));
        return panel;
    }
}
