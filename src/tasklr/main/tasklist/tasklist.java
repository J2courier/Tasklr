package tasklr.main.tasklist;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.createPanel;

import java.awt.*;
public class tasklist {
    public static JPanel createTaskList() { //create task list is a subpanel of a homePanel method
        //parent panel
        JPanel panel = createPanel.panel(new Color(0xE0E3E2), new BorderLayout(), new Dimension(325, 0));
        Border border = BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(0x749AAD));
        panel.setBorder(border);
        //child panels
        JPanel panel_header = createPanel.panel(null, new FlowLayout(FlowLayout.CENTER, 20, 15), new Dimension(0, 50));
        Border header_border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x749AAD));
        panel_header.setBorder(header_border);
        JPanel panel_body = createPanel.panel(null, new BorderLayout(), new Dimension(0, 0));
        
        //label of header
        JLabel panel_header_lbl = new JLabel("TASK LIST");
        panel_header_lbl.setFont(new Font("Arial", Font.BOLD, 24));
        panel_header_lbl.setForeground(new Color(0x464646));

        //adding component into the panel
        panel_header.add(panel_header_lbl);//add label into the header
        panel.add(panel_header, BorderLayout.NORTH);//add header into parent panel
        panel.add(panel_body, BorderLayout.CENTER);//add body into the parent panel
         
        return panel;
    }
}
