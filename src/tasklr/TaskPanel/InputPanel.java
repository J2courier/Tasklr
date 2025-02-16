package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.createButton;
import tasklr.createPanel;
import java.awt.*;
public class InputPanel {
    
    public static JPanel createInputPanel(){
        //input field container
        JPanel inputContainer = createPanel.panel(new Color(0x292E34), new GridBagLayout(), new Dimension(700, 1100));
        Border inputContainerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        
        //input component for adding title
        JLabel addTitle = new JLabel("ADD TITLE");
        addTitle.setForeground(Color.WHITE);
        addTitle.setBorder(inputContainerBorder);
        addTitle.setFont(new Font("Arial", Font.BOLD, 24));
        addTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        JTextField title = new JTextField(50);

        //input component for adding description
        JLabel addDescription = new JLabel("Add Description");
        addDescription.setForeground(Color.WHITE);
        addDescription.setBorder(inputContainerBorder);
        addDescription.setFont(new Font("Arial", Font.BOLD, 24));
        addDescription.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        JTextField description = new JTextField(50);

        //input component for adding category
        JLabel addCategory = new JLabel("Add Description");
        addCategory.setForeground(Color.WHITE);
        addCategory.setBorder(inputContainerBorder);
        addCategory.setFont(new Font("Arial", Font.BOLD, 24));
        addCategory.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        String[] options = {"Shopping Task", "Marketing Task", "Sales Task", "Urgent Task", "Personal Task"};
        JComboBox<String> category = new JComboBox<>(options);

        
        JButton cancelBtn = createButton.button("cancel", new Color(0x484848), Color.WHITE, null, false);
        JButton AddBtn = createButton.button("Add Task", new Color(0x484848), Color.WHITE, null, false);


        addComponent(inputContainer, addTitle, 0, 0, 2, 1, new Insets(5, 5, 5, 5));
        addComponent(inputContainer, title, 0, 1, 2, 1, new Insets(5, 5, 5, 5));
        addComponent(inputContainer, addDescription, 0, 2, 2, 1, new Insets(5, 5, 5, 5));
        addComponent(inputContainer, description, 0, 3, 2, 1, new Insets(5, 5, 5, 5));
        addComponent(inputContainer, addCategory, 0, 4, 2, 1, new Insets(5, 5, 5, 5));
        addComponent(inputContainer, category, 0, 5, 2, 1, new Insets(5, 5, 5, 5));
        addComponent(inputContainer, cancelBtn, 0, 6, 1, 1, new Insets(5, 5, 5, 5));
        addComponent(inputContainer, AddBtn, 1, 6, 2, 1, new Insets(5, 5, 5, 5));

        return inputContainer;
    }

    private static void addComponent(JPanel panel, JComponent comp, int x, int y, int width, int height, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = insets; // Use custom insets
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        panel.add(comp, gbc);
    }
}
