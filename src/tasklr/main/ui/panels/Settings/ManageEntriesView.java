package tasklr.main.ui.panels.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tasklr.utilities.createPanel;
import tasklr.utilities.Toast;
import tasklr.utilities.DatabaseManager;
import tasklr.authentication.UserSession;
import tasklr.main.ui.panels.Home.HomePanel;
import tasklr.main.ui.panels.TaskPanel.task;
import tasklr.main.ui.panels.quizPanel.FlashcardPanel;
import tasklr.main.ui.panels.quizPanel.FlashcardUIRefresher;

public class ManageEntriesView {
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    
    public static JPanel createPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        
        // Header
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Back button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(e -> SettingsPanel.showMainView());
        
        JLabel titleLabel = new JLabel("Manage Entries");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create entries management sections using GridBagLayout
        JPanel sectionsPanel = new JPanel(new GridBagLayout());
        sectionsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0); // Space between sections

        // Tasks section
        gbc.gridy = 0;
        sectionsPanel.add(createSection("Tasks", "Manage your tasks and categories. Delete multiple task in at once."), gbc);
        
        // Flashcard Sets section
        gbc.gridy = 1;
        sectionsPanel.add(createSection("Flashcard Sets", "Manage your flashcard sets. Delete multiple flashcard sets at once."), gbc);
        
        // Flashcards section
        gbc.gridy = 2;
        sectionsPanel.add(createSection("Flashcards", "Manage individual flashcards. Delete multiple individual flashcards at once."), gbc);
        
        // Add spacer panel
        gbc.gridy = 3;
        gbc.weighty = 1.0; // Make the spacer expand vertically
        gbc.fill = GridBagConstraints.BOTH;
        JPanel spacerPanel = new JPanel();
        spacerPanel.setBackground(Color.WHITE);
        sectionsPanel.add(spacerPanel, gbc);
        
        JScrollPane scrollPane = new JScrollPane(sectionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel createSection(String title, String description) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;

        // Header panel with title and manage button
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JButton manageButton = new JButton("Manage");
        manageButton.setBackground(new Color(0x0065D9));
        manageButton.setForeground(Color.WHITE);
        manageButton.setFocusPainted(false);
        manageButton.addActionListener(e -> handleManage(title));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(manageButton, BorderLayout.EAST);
        
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(headerPanel, gbc);

        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(descLabel, gbc);

        // Filler panel to take up remaining space
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createVerticalGlue(), gbc);

        // Now we can control height through the parent's constraints
        panel.setPreferredSize(new Dimension(0, 200)); // Default height, but can be overridden by parent

        return panel;
    }
    
    private static void handleManage(String section) {
        if ("Tasks".equals(section)) {
            try {
                // Fetch all tasks for the current user
                String query = "SELECT title FROM tasks WHERE user_id = ? ORDER BY title ASC";
                ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());
                
                // Create a list to store task titles and checkboxes
                List<String> taskTitles = new ArrayList<>();
                List<JCheckBox> checkBoxes = new ArrayList<>();
                
                while (rs.next()) {
                    taskTitles.add(rs.getString("title"));
                }
                
                if (taskTitles.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        null,
                        "No tasks found to manage.",
                        "No Tasks",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                // Create panel for checkboxes with GridBagLayout
                JPanel checkBoxPanel = new JPanel(new GridBagLayout());
                checkBoxPanel.setBackground(Color.WHITE);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(2, 5, 2, 5);

                // Add checkboxes for each task
                for (String title : taskTitles) {
                    JCheckBox checkBox = new JCheckBox(title);
                    checkBox.setBackground(Color.WHITE);
                    checkBoxes.add(checkBox);
                    checkBoxPanel.add(checkBox, gbc);
                }

                // Create scroll pane for the checkboxes
                JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                scrollPane.setBackground(Color.WHITE);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());

                // Create custom panel for the dialog
                JPanel panel = new JPanel(new BorderLayout(0, 10));
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Add header label
                JLabel headerLabel = new JLabel("Select tasks to delete:");
                headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
                panel.add(headerLabel, BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);

                // Create custom buttons
                Object[] options = {"Delete Selected", "Cancel"};
                
                // Show custom JOptionPane without warning icon
                int result = JOptionPane.showOptionDialog(
                    null,
                    panel,
                    "Manage Tasks",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, // Changed to PLAIN_MESSAGE to remove warning icon
                    null,
                    options,
                    options[1] // default to Cancel
                );

                if (result == JOptionPane.OK_OPTION) {
                    // Get selected tasks
                    List<String> selectedTasks = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            selectedTasks.add(taskTitles.get(i));
                        }
                    }
                    
                    if (selectedTasks.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null,
                            "No tasks selected for deletion.",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    // Confirm deletion
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete " + selectedTasks.size() + " selected task(s)?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            // Create the SQL query with multiple placeholders
                            StringBuilder deleteQuery = new StringBuilder(
                                "DELETE FROM tasks WHERE user_id = ? AND title IN ("
                            );
                            for (int i = 0; i < selectedTasks.size(); i++) {
                                deleteQuery.append(i > 0 ? ", ?" : "?");
                            }
                            deleteQuery.append(")");

                            // Create array of parameters
                            Object[] params = new Object[selectedTasks.size() + 1];
                            params[0] = UserSession.getUserId();
                            for (int i = 0; i < selectedTasks.size(); i++) {
                                params[i + 1] = selectedTasks.get(i);
                            }

                            // Execute the delete query
                            DatabaseManager.executeUpdate(deleteQuery.toString(), params);

                            Toast.success(selectedTasks.size() + " task(s) deleted successfully!");
                            
                            // Refresh necessary panels
                            task.refreshTaskContainer();
                            HomePanel.refreshTasksList();
                            
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            Toast.error("Error deleting tasks: " + ex.getMessage());
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Toast.error("Error fetching tasks: " + ex.getMessage());
            }
        } else if ("Flashcard Sets".equals(section)) {
            try {
                // Fetch all flashcard sets for the current user
                String query = "SELECT set_id, subject FROM flashcard_sets WHERE user_id = ? ORDER BY subject ASC";
                ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());
                
                // Create lists to store set information and checkboxes
                List<Map<String, Object>> setInfo = new ArrayList<>();
                List<JCheckBox> checkBoxes = new ArrayList<>();
                
                while (rs.next()) {
                    Map<String, Object> set = new HashMap<>();
                    set.put("set_id", rs.getInt("set_id"));
                    set.put("subject", rs.getString("subject"));
                    setInfo.add(set);
                }
                
                if (setInfo.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        null,
                        "No flashcard sets found to manage.",
                        "No Flashcard Sets",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                // Create panel for checkboxes with GridBagLayout
                JPanel checkBoxPanel = new JPanel(new GridBagLayout());
                checkBoxPanel.setBackground(Color.WHITE);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(2, 5, 2, 5);

                // Add checkboxes for each flashcard set
                for (Map<String, Object> set : setInfo) {
                    JCheckBox checkBox = new JCheckBox(set.get("subject").toString());
                    checkBox.setBackground(Color.WHITE);
                    checkBoxes.add(checkBox);
                    checkBoxPanel.add(checkBox, gbc);
                }

                // Create scroll pane for the checkboxes
                JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                scrollPane.setBackground(Color.WHITE);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());

                // Create custom panel for the dialog
                JPanel panel = new JPanel(new BorderLayout(0, 10));
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Add header label
                JLabel headerLabel = new JLabel("Select flashcard sets to delete:");
                headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
                panel.add(headerLabel, BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);

                // Create custom buttons
                Object[] options = {"Delete Selected", "Cancel"};
                
                // Show custom JOptionPane
                int result = JOptionPane.showOptionDialog(
                    null,
                    panel,
                    "Manage Flashcard Sets",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[1]
                );

                if (result == JOptionPane.OK_OPTION) {
                    // Get selected flashcard sets
                    List<Integer> selectedSetIds = new ArrayList<>();
                    List<String> selectedSubjects = new ArrayList<>();
                    
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            selectedSetIds.add((Integer) setInfo.get(i).get("set_id"));
                            selectedSubjects.add((String) setInfo.get(i).get("subject"));
                        }
                    }
                    
                    if (selectedSetIds.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null,
                            "No flashcard sets selected for deletion.",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    // Show confirmation dialog
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Deleting Flashcard set will lead to permanent deletion of Flashcard item Terms and Definition of a flashcard sets.\n\n" +
                        "Selected sets to delete:\n" + String.join("\n", selectedSubjects) + "\n\n" +
                        "Are you sure to delete these sets?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        handleFlashcardSetDeletion(selectedSetIds, selectedSubjects);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Toast.error("Error fetching flashcard sets: " + ex.getMessage());
            }
        }
    }

    private static void handleFlashcardSetDeletion(List<Integer> selectedSetIds, List<String> selectedSubjects) {
        try {
            Connection conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            
            try {
                // First delete all flashcards associated with the selected sets
                String deleteFlashcardsQuery = "DELETE FROM flashcards WHERE set_id IN (" +
                    selectedSetIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";
                
                PreparedStatement flashcardsStmt = conn.prepareStatement(deleteFlashcardsQuery);
                for (int i = 0; i < selectedSetIds.size(); i++) {
                    flashcardsStmt.setInt(i + 1, selectedSetIds.get(i));
                }
                flashcardsStmt.executeUpdate();

                // Then delete the flashcard sets
                String deleteSetsQuery = "DELETE FROM flashcard_sets WHERE set_id IN (" +
                    selectedSetIds.stream().map(id -> "?").collect(Collectors.joining(",")) + 
                    ") AND user_id = ?";
                
                PreparedStatement setsStmt = conn.prepareStatement(deleteSetsQuery);
                int paramIndex = 1;
                for (Integer setId : selectedSetIds) {
                    setsStmt.setInt(paramIndex++, setId);
                }
                setsStmt.setInt(paramIndex, UserSession.getUserId());
                setsStmt.executeUpdate();

                conn.commit();
                Toast.success(selectedSetIds.size() + " flashcard set(s) and their contents deleted successfully!");
                
                // Try to refresh UI, but don't throw exception if it fails
                try {
                    FlashcardUIRefresher.refreshListContainer();
                    FlashcardUIRefresher.refreshFlashcardMode(FlashcardPanel.getCurrentSetId());
                } catch (Exception e) {
                    // Log the error but don't let it affect the deletion operation
                    System.err.println("Warning: Could not refresh flashcard UI: " + e.getMessage());
                }
                
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error deleting flashcard sets: " + ex.getMessage());
        }
    }
}
