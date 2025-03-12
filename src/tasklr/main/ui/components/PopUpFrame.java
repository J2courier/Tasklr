package tasklr.main.ui.components;

import javax.swing.*;

import tasklr.utilities.createPanel;

import java.awt.*;
public class PopUpFrame extends JFrame{
    public PopUpFrame(String title){
        setTitle(title);
        setSize(700, 920);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);
        setMinimumSize(new Dimension(900, 1000));
        getContentPane().setBackground(new Color(0xf1f3f6));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        

    }

    public static JPanel PopUpComponent(String labelValue) {
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 70));
        
        JLabel label = new JLabel(labelValue, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
}
