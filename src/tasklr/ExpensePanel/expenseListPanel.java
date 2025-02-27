package tasklr.ExpensePanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import tasklr.createButton;
import tasklr.createPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class expenseListPanel {
    private JPanel taskContainerScrollPanel;
    private JPanel expensePanel;
    private JPanel inputPanel; // Reference to the input panel

    public expenseListPanel() {
        // Task list container
        expensePanel = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(1000, 1125));
        Border expenseContainerBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x6D6D6D));
        expensePanel.setBorder(expenseContainerBorder);

        // Header
        JPanel taskContainerHeader = createPanel.panel(new Color(0x191919), new BorderLayout(), new Dimension(0, 70));

        JLabel taskContainerLabel = new JLabel("ADDED EXPENSES");
        taskContainerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        taskContainerLabel.setForeground(new Color(0xe8eaed));
        taskContainerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        taskContainerHeader.add(taskContainerLabel, BorderLayout.CENTER);

        JButton addTaskBtn = createButton.button("ADD", null, new Color(0xe8eaed), null, false);
        addTaskBtn.setPreferredSize(new Dimension(100, 0));
        addTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputPanel != null) {
                    inputPanel.setVisible(!inputPanel.isVisible());
                }
            }
        });
        taskContainerHeader.add(addTaskBtn, BorderLayout.EAST);

        // Scrollable Task Panel
        taskContainerScrollPanel = new JPanel();
        taskContainerScrollPanel.setLayout(new BoxLayout(taskContainerScrollPanel, BoxLayout.Y_AXIS));
        taskContainerScrollPanel.setBackground(new Color(0xf1f3f6));

        JScrollPane scrollPane = new JScrollPane(taskContainerScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        expensePanel.add(taskContainerHeader, BorderLayout.NORTH);
        expensePanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getTaskPanel() {
        return expensePanel;
    }

    public void setInputPanel(JPanel inputPanel) {
        this.inputPanel = inputPanel;
    }

    public void addTask(String title, String desc, String category) {
        // Create a panel for the task
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        taskPanel.setBackground(new Color(0xE0E3E2)); // Match background color
    
        // Title label
        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 10));
    
        // Description label
        JLabel descLabel = new JLabel("<html>" + desc + "</html>");
        descLabel.setForeground(Color.BLACK);
    
        // Add components to the task panel
        taskPanel.add(titleLabel);
        taskPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
    
        // Add the task panel to the scrollable container
        taskContainerScrollPanel.add(taskPanel);
        taskContainerScrollPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
    
        // Revalidate and repaint
        taskContainerScrollPanel.revalidate();
        taskContainerScrollPanel.repaint();
    }
}
