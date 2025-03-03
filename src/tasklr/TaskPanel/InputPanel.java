package tasklr.TaskPanel;

    import javax.swing.*;
    import javax.swing.border.Border;
    import tasklr.createButton;
    import tasklr.createPanel;
    import tasklr.authentication.UserSession;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.PreparedStatement;
    import java.sql.SQLException;
    import java.sql.ResultSet;
    //? FONT: Segoe UI Variable
    public class InputPanel {
        private JPanel inputPanel;
        private TaskListPanel taskListPanel; // Reference to TaskListPanel
        
        public InputPanel(TaskListPanel taskListPanel) {
            this.taskListPanel = taskListPanel;
            

            inputPanel = createPanel.panel(null, new GridBagLayout(), new Dimension(700, 1100));
            inputPanel.setVisible(false); // Set visibility to false initially

            JLabel addTitle = new JLabel("ADD TITLE");
            addTitle.setForeground(Color.BLACK);
            // addTitle.setBorder(inputContainerBorder);
            addTitle.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
            addTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            
            JTextField title = new JTextField(50);
            Border titleBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x44759D));
            title.setBackground(null);
            title.setForeground(Color.BLACK);
            title.setBorder(titleBorder);
            title.setCaretColor(Color.BLACK);
            title.setFont(new Font("Arial", Font.PLAIN, 16));
            title.setPreferredSize(new Dimension(0, 30));

        

            JLabel setStatus = new JLabel("SET STATUS");
            setStatus.setForeground(Color.BLACK);
            // setStatus.setBorder(inputContainerBorder);
            setStatus.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
            setStatus.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            String[] options = {"PENDING", "COMPLETED"};
            JComboBox<String> category = new JComboBox<>(options);
            category.setPreferredSize(new Dimension(0, 30));

            JPanel spacer = createPanel.panel(null, null, new Dimension(200, 200));

            JButton AddBtn = createButton.button("Add Task", new Color(0x2E5AEA), Color.WHITE, null, false);
            AddBtn.setPreferredSize(new Dimension(100, 40));
            JButton cancelBtn = createButton.button("Close", new Color(0xC5C5C5), Color.BLACK, null, false);
            cancelBtn.setPreferredSize(new Dimension(100, 40));

            // Add Task Button Action
            AddBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String taskTitle = title.getText();
                    String selectedCategory = (String) category.getSelectedItem();
            
                    if (!taskTitle.isEmpty() && !selectedCategory.isEmpty()) {
                        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tasklrdb", "JFCompany", "")) {
                            String sql = "INSERT INTO tasks (user_id, title, status) VALUES (?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setInt(1, UserSession.getUserId());
                                pstmt.setString(2, taskTitle);
                                pstmt.setString(3, selectedCategory);
                                pstmt.executeUpdate();
                                JOptionPane.showMessageDialog(inputPanel, "Added Successfully!");
                                
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
            
                        // Refresh task list
                        taskListPanel.fetchAndDisplayTasks();
                        title.setText("");
                        inputPanel.setVisible(false);
                    }
                }
            });
            

            cancelBtn.addActionListener(e -> {
                title.setText("");
                
                inputPanel.setVisible(false); // Hide the input panel
            });

            addComponent(inputPanel, addTitle, 0, 0, 2, 1, new Insets(50, 15, 5, 15), 0.0);
            addComponent(inputPanel, title, 0, 1, 2, 1, new Insets(5, 15, 20, 15), 0.0);// Use scroll pane
            addComponent(inputPanel, setStatus, 0, 4, 2, 1, new Insets(5, 15, 5, 15), 0.0);
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
