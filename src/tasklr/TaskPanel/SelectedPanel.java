package tasklr.TaskPanel;

import javax.swing.*;

import tasklr.createButton;
import tasklr.createPanel;
import tasklr.setProgress;

import java.awt.*;
public class SelectedPanel {
    
    public JPanel createEditPanel() { // Corrected method name
        JPanel panel = createPanel.panel(null, new GridBagLayout(), new Dimension(700, 1100));
        panel.setVisible(false); 
        JLabel Title = new JLabel("ADD TITLE");
        Title.setFont(new Font("Segoe UI Variable", Font.BOLD, 34));

        JLabel setStatus = new JLabel("SET STATUS");
        setStatus.setForeground(Color.BLACK);
        setStatus.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        setStatus.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        String[] options = {"COMPLETED"};
        JComboBox<String> status = new JComboBox<>(options);
        status.setPreferredSize(new Dimension(0, 30));

        JButton delBtn = createButton.button("DELETE", new Color(0xEB3C3C), Color.WHITE, null, false);
        delBtn.setPreferredSize(new Dimension(100, 40));
        JButton updateBtn = createButton.button("UPDATE", new Color(0x1BD827), Color.WHITE, null, false);
        updateBtn.setPreferredSize(new Dimension(100, 40));

        JPanel spacer = createPanel.panel(null, null, new Dimension(200, 200));


        addComponent(panel, Title, 0, 0, 2, 1, new Insets(50, 15, 25, 15), 0.0);
        addComponent(panel, setStatus, 0, 1, 2, 1, new Insets(5, 15, 5, 15), 0.0);
        addComponent(panel, status, 0, 2, 2, 1, new Insets(5, 15, 25, 15), 0.0);
        addComponent(panel, delBtn, 0, 3, 1, 1, new Insets(5, 15, 20, 15), 0.0); 
        addComponent(panel, updateBtn, 1, 3, 1, 1, new Insets(5, 15, 20, 15), 0.0);
        addComponent(panel, spacer, 0, 4, 2, 1, new Insets(5, 15, 5, 15), 1.0);
       

        return panel;
    }

    private static void addComponent(JPanel panel, JComponent comp, int x, int y, int width, int height, Insets insets, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = weighty;
        gbc.insets = insets; // Use custom insets
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(comp, gbc);
    }
}