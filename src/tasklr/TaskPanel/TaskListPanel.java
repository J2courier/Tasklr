package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.createPanel;
import java.awt.*;

public class TaskListPanel {
    private JPanel taskContainerScrollPanel;
    private JPanel taskPanel;

    public TaskListPanel() {
        // Task list container
        taskPanel = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(500, 1100));
        Border taskContainerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        taskPanel.setBorder(taskContainerBorder);

        // Header
        JPanel taskContainerHeader = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(0, 70));
        Border taskContainerHeaderBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        taskContainerHeader.setBorder(taskContainerHeaderBorder);

        JLabel taskContainerLabel = new JLabel("ADDED LIST");
        taskContainerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        taskContainerLabel.setForeground(Color.WHITE);
        taskContainerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        taskContainerHeader.add(taskContainerLabel, BorderLayout.CENTER);

        // Scrollable Task Panel
        taskContainerScrollPanel = new JPanel();
        taskContainerScrollPanel.setLayout(new BoxLayout(taskContainerScrollPanel, BoxLayout.Y_AXIS));
        taskContainerScrollPanel.setBackground(new Color(0x0A4A7E));

        JScrollPane scrollPane = new JScrollPane(taskContainerScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        Border scrollPaneBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        scrollPane.setBorder(scrollPaneBorder);

        taskPanel.add(taskContainerHeader, BorderLayout.NORTH);
        taskPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getTaskPanel() {
        return taskPanel;
    }

    public void addTask(String title, String desc) {
        JPanel taskPanel = createPanel.panel(null, null, new Dimension(0, 0));
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        taskPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setForeground(Color.WHITE);
        JLabel descLabel = new JLabel("<html>" + desc + "</html>");
        descLabel.setForeground(Color.WHITE);

        taskPanel.add(titleLabel);
        taskPanel.add(descLabel);

        taskContainerScrollPanel.add(taskPanel);
        taskContainerScrollPanel.revalidate();
        taskContainerScrollPanel.repaint();
    }
}
