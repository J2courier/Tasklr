package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.createButton;
import tasklr.createPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputPanel {
    private JPanel inputPanel;
    private TaskListPanel taskListPanel; // Reference to TaskListPanel

    public InputPanel(TaskListPanel taskListPanel) {
        this.taskListPanel = taskListPanel;

        inputPanel = createPanel.panel(null, new GridBagLayout(), new Dimension(700, 1100));

        JLabel addTitle = new JLabel("ADD TITLE");
        addTitle.setForeground(Color.BLACK);
        // addTitle.setBorder(inputContainerBorder);
        addTitle.setFont(new Font("Arial", Font.BOLD, 16));
        addTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        JTextField title = new JTextField(50);
        Border titleBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x44759D));
        title.setBackground(null);
        title.setForeground(Color.BLACK);
        title.setBorder(titleBorder);
        title.setCaretColor(Color.BLACK);
        title.setFont(new Font("Arial", Font.PLAIN, 16));
        title.setPreferredSize(new Dimension(0, 30));

        JLabel addDescription = new JLabel("ADD DESCRIPTION");
        addDescription.setForeground(Color.BLACK);
        // addDescription.setBorder(inputContainerBorder);
        addDescription.setFont(new Font("Arial", Font.BOLD, 16));
        addDescription.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JTextArea description = new JTextArea(1, 50); // Set rows and columns
        description.setBackground(null);
        description.setForeground(Color.BLACK);
        description.setCaretColor(Color.BLACK);
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setLineWrap(true); // Enable line wrapping
        description.setWrapStyleWord(true); // Wrap at word boundaries
        JScrollPane descriptionScrollPane = new JScrollPane(description);
        descriptionScrollPane.setPreferredSize(new Dimension(0, 100)); // Set preferred size for the scroll pane
        Border descriptionBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0xB7B7B7));
        descriptionScrollPane.setBorder(descriptionBorder);

        JLabel addCategory = new JLabel("ADD CATEGORY");
        addCategory.setForeground(Color.BLACK);
        // addCategory.setBorder(inputContainerBorder);
        addCategory.setFont(new Font("Arial", Font.BOLD, 16));
        addCategory.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        String[] options = {"Shopping Task", "Marketing Task", "Sales Task", "Urgent Task", "Personal Task"};
        JComboBox<String> category = new JComboBox<>(options);
        category.setPreferredSize(new Dimension(0, 30));

        JPanel spacer = createPanel.panel(null, null, new Dimension(200, 200));

        JButton AddBtn = createButton.button("Add Task", new Color(0x2E5AEA), Color.WHITE, null, false);
        AddBtn.setPreferredSize(new Dimension(100, 40));
        JButton cancelBtn = createButton.button("Cancel", new Color(0xC5C5C5), Color.BLACK, null, false);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        // Add Task Button Action
        AddBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String taskTitle = title.getText();
                String taskDescription = description.getText();
                String selectedCategory = (String) category.getSelectedItem(); // Get selected category
                if (!taskTitle.isEmpty() && !taskDescription.isEmpty()) {
                    taskListPanel.addTask(taskTitle, taskDescription, selectedCategory); // Pass to TaskListPanel
                    title.setText("");
                    description.setText("");
                }
            }
        });

        cancelBtn.addActionListener(e -> {
            title.setText("");
            description.setText("");
        });

        addComponent(inputPanel, addTitle, 0, 0, 2, 1, new Insets(50, 15, 5, 15), 0.0);
        addComponent(inputPanel, title, 0, 1, 2, 1, new Insets(5, 15, 20, 15), 0.0);
        addComponent(inputPanel, addDescription, 0, 2, 2, 1, new Insets(5, 15, 5, 15), 0.0);
        addComponent(inputPanel, descriptionScrollPane, 0, 3, 2, 1, new Insets(5, 15, 20, 15), 0.1); // Use scroll pane
        addComponent(inputPanel, addCategory, 0, 4, 2, 1, new Insets(5, 15, 5, 15), 0.0);
        addComponent(inputPanel, category, 0, 5, 2, 1, new Insets(5, 15, 20, 15), 0.0);
        addComponent(inputPanel, cancelBtn, 0, 6, 1, 1, new Insets(5, 15, 300, 5), 0.0);
        addComponent(inputPanel, AddBtn, 1, 6, 1, 1, new Insets(5, 5, 300, 15), 0.0);
        addComponent(inputPanel, spacer, 0, 7, 2, 1, new Insets(5, 5, 5, 15), 0.0);

    }

    public JPanel getInputPanel() {
        return inputPanel;
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
