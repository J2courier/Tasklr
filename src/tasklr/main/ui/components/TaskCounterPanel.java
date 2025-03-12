package tasklr.main.ui.components;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.utilities.createPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TaskCounterPanel {
    private int taskCount;
    private JLabel countLabel;
    private String taskType;

    public TaskCounterPanel(int taskCount, String taskType) {
        this.taskCount = taskCount;
        this.taskType = taskType;
    }

    public JPanel createPanel() {
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 300));
        Border panelBorder = BorderFactory.createLineBorder(new Color(0xB9B9B9), 1);
        panel.setBorder(panelBorder);

        JPanel countPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 170));
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xB9B9B9));
        countPanel.setBorder(border);

        JPanel labelPanel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 50));

        countLabel = new JLabel(String.valueOf(taskCount), SwingConstants.CENTER);
        countLabel.setForeground(new Color(0x414141));
        countLabel.setFont(new Font("Arial", Font.BOLD, 50));
        countPanel.add(countLabel, BorderLayout.CENTER);

        JLabel taskLabel = new JLabel(taskType.toUpperCase(), SwingConstants.CENTER);
        taskLabel.setForeground(Color.BLACK);
        taskLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        taskLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new PopUpFrame(taskLabel.getText()).setVisible(true);
            }
        });
        labelPanel.add(taskLabel, BorderLayout.CENTER);

        // Add the components
        panel.add(countPanel, BorderLayout.CENTER);
        panel.add(labelPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void updateTaskCount(int newCount) {
        this.taskCount = newCount;
        if (countLabel != null) {
            countLabel.setText(String.valueOf(newCount));
        }
    }

    public void updateCount(int newCount) {
        this.taskCount = newCount;
        if (countLabel != null) {
            countLabel.setText(String.valueOf(newCount));
        }
    }

}
