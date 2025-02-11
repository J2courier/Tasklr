package tasklr;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
public class tasklist {
    public static JPanel createTaskList() { //create task list is a subpanel of a homePanel method
        //parent panel
        JPanel panel = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(350, 0));
        Border border = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        panel.setBorder(border);
        //child panels
        JPanel panel_header = createPanel.panel(null, new FlowLayout(FlowLayout.CENTER, 20, 15), new Dimension(0, 50));
        Border header_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        panel_header.setBorder(header_border);
        JPanel panel_body = createPanel.panel(null, new BorderLayout(), new Dimension(0, 0));
        
        //label of header
        JLabel panel_header_lbl = new JLabel("Task List");
        panel_header_lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        panel_header_lbl.setForeground(Color.WHITE);

        //adding component into the panel
        panel_header.add(panel_header_lbl);//add label into the header
        panel.add(panel_header, BorderLayout.NORTH);//add header into parent panel
        panel.add(panel_body, BorderLayout.CENTER);//add body into the parent panel
         
        return panel;
    }
}
