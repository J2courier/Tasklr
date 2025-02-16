package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.createButton;
import tasklr.createPanel;
import java.awt.*;

public class InputPanel {

    public static JPanel createInputPanel() {
        // Input field container
        JPanel inputContainer = createPanel.panel(new Color(0x292E34), new GridBagLayout(), new Dimension(700, 1100));
        Border inputContainerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        inputContainer.setBorder(inputContainerBorder);

        // Input component for adding title
        JLabel addTitle = new JLabel("ADD TITLE");
        addTitle.setForeground(new Color(0xB7B7B7));
        addTitle.setBorder(inputContainerBorder);
        addTitle.setFont(new Font("Arial", Font.BOLD, 16));
        addTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        JTextField title = new JTextField(50);
        Border titleBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xB7B7B7));
        title.setBackground(new Color(0x292E34));
        title.setForeground(Color.WHITE);
        title.setBorder(titleBorder);
        title.setCaretColor(Color.WHITE);
        // title.setFont(new Font("Arial", Font.PLAIN, 20));
        title.setPreferredSize(new Dimension(0, 40));

        // Input component for adding description
        JLabel addDescription = new JLabel("ADD DESCRIPTION");
        addDescription.setForeground(new Color(0xB7B7B7));
        addDescription.setBorder(inputContainerBorder);
        addDescription.setFont(new Font("Arial", Font.BOLD, 16));
        addDescription.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        JTextArea description = new JTextArea(1, 50); // Set rows and columns
        Border descriptionBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0xB7B7B7));
        description.setBackground(new Color(0x292E34));
        description.setForeground(Color.WHITE);
        description.setBorder(descriptionBorder);
        description.setCaretColor(Color.WHITE);
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setLineWrap(true); // Enable line wrapping
        description.setWrapStyleWord(true); // Wrap at word boundaries
        JScrollPane descriptionScrollPane = new JScrollPane(description);
        descriptionScrollPane.setPreferredSize(new Dimension(0, 100)); // Set preferred size for the scroll pane

        // Input component for adding category
        JLabel addCategory = new JLabel("ADD CATEGORY");
        addCategory.setForeground(new Color(0xB7B7B7));
        addCategory.setBorder(inputContainerBorder);
        addCategory.setFont(new Font("Arial", Font.BOLD, 16));
        addCategory.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        String[] options = {"Shopping Task", "Marketing Task", "Sales Task", "Urgent Task", "Personal Task"};
        JComboBox<String> category = new JComboBox<>(options);
        category.setPreferredSize(new Dimension(0, 30));

        // Buttons
        // Cancel button
        JButton cancelBtn = createButton.button("cancel", new Color(0x484848), Color.WHITE, null, false);
        Border cancelBtnBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        cancelBtn.setBorder(cancelBtnBorder);
        cancelBtn.setPreferredSize(new Dimension(100, 40));

        // Add button
        JButton AddBtn = createButton.button("Add Task", new Color(0x007806), Color.WHITE, null, false);
        Border addBtnBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        AddBtn.setBorder(addBtnBorder);
        AddBtn.setPreferredSize(new Dimension(100, 40));

        // Additional panel
        JPanel spacer = createPanel.panel(null, null, new Dimension(200, 200));

        // Adding the component into gbc
        addComponent(inputContainer, addTitle, 0, 0, 2, 1, new Insets(50, 15, 5, 15), 0.0);
        addComponent(inputContainer, title, 0, 1, 2, 1, new Insets(5, 15, 20, 15), 0.0);
        addComponent(inputContainer, addDescription, 0, 2, 2, 1, new Insets(5, 15, 5, 15), 0.0);
        addComponent(inputContainer, descriptionScrollPane, 0, 3, 2, 1, new Insets(5, 15, 20, 15), 0.1); // Use scroll pane
        addComponent(inputContainer, addCategory, 0, 4, 2, 1, new Insets(5, 15, 5, 15), 0.0);
        addComponent(inputContainer, category, 0, 5, 2, 1, new Insets(5, 15, 20, 15), 0.0);
        addComponent(inputContainer, cancelBtn, 0, 6, 1, 1, new Insets(5, 15, 300, 5), 0.0);
        addComponent(inputContainer, AddBtn, 1, 6, 1, 1, new Insets(5, 5, 300, 15), 0.0);
        addComponent(inputContainer, spacer, 0, 7, 2, 1, new Insets(5, 5, 5, 15), 0.0);

        return inputContainer;
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