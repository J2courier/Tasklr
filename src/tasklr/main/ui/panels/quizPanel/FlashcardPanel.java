
package tasklr.main.ui.panels.quizPanel;


import javax.swing.*;
import javax.swing.border.Border;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class FlashcardPanel {
    private static JPanel quizContainer;
    private static JScrollPane scrollPane;
    private static CardLayout cardLayout;
    private static JPanel mainCardPanel;
    // private static JPanel inputPanel;
    private static final Color TEXT_COLOR = new Color(0x242424);
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    // private static final Color TEXTFIELD_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_CONTAINER_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_HOVER_BG = new Color(0xF5F5F5);
    private static final Color LIST_ITEM_HOVER_BORDER = new Color(0x0082FC);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0x275CE2);
    private static final Color PRIMARY_BUTTON_HOVER = new Color(0x3B6FF0);
    private static final Color PRIMARY_BUTTON_TEXT = Color.WHITE;
    private static int currentSetId = -1; // Track current set ID
    private static String currentSubject = ""; // Track current subject
    
    // Add the getter method for currentSetId
    public static int getCurrentSetId() {
        return currentSetId;
    }
    
    // Add the setter method for completeness
    public static void setCurrentSetId(int setId) {
        currentSetId = setId;
    }
    
    // Temporary storage for terms before set creation
    private static List<Map<String, String>> temporaryTerms = new ArrayList<>();
    
    private static boolean isCardView = false;
    private static int currentCardIndex = 0;
    private static List<Map<String, String>> flashcardsList = new ArrayList<>();
    private static JLabel termsInputTitleLabel;
    private static JPanel listContainer;
    private static JButton addMoreTermsBtn;
    private static JButton viewToggleBtn;
    private static List<JButton> toggleButtons = new ArrayList<>();
    private static boolean isListVisible = true;
    private static JButton setCreationToggleBtn; // Store reference to set creation panel's toggle button

    // Add a static initialization method
    public static void initializeUIRefresher(JPanel quizContainer, JPanel mainCardPanel, 
                                           CardLayout cardLayout, JScrollPane scrollPane) {
        FlashcardUIRefresher.initialize(quizContainer, mainCardPanel, cardLayout, scrollPane);
    }

    public static JPanel createFlashcardPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));

        JPanel contentPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        
        mainCardPanel = new JPanel();
        cardLayout = new CardLayout();
        mainCardPanel.setLayout(cardLayout);
        
        // Create three panels: set creation, terms input, and flashcard mode
        JPanel setCreationPanel = createSetCreationPanel();
        JPanel termsInputPanel = createTermsInputPanel();
        JPanel flashcardModePanel = createFlashcardModePanel(-1); // Pass -1 for initial state
        
        mainCardPanel.add(setCreationPanel, "setCreation");
        mainCardPanel.add(termsInputPanel, "termsInput");
        mainCardPanel.add(flashcardModePanel, "flashcardMode");
        
        listContainer = createListContainer();
        
        contentPanel.add(mainCardPanel, BorderLayout.CENTER);
        contentPanel.add(listContainer, BorderLayout.WEST);
        
        // Initialize FlashcardUIRefresher right after creating all necessary components
        initializeUIRefresher(quizContainer, mainCardPanel, cardLayout, scrollPane);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private static JPanel createSetCreationPanel() {
        JPanel panel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left side of header with title
        JLabel titleLabel = new JLabel("CREATE FLASHCARD SET");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0x1d1d1d));
        
        // Right side of header with toggle button
        setCreationToggleBtn = createButton.button(isListVisible ? "Hide List" : "Show List", null, Color.WHITE, null, false);
        setCreationToggleBtn.setBackground(new Color(0x275CE2));
        setCreationToggleBtn.setPreferredSize(new Dimension(120, 40));
        
        // Add hover effect
        new HoverButtonEffect(setCreationToggleBtn, 
            new Color(0x275CE2),  // default background
            new Color(0x1E40AF),  // hover background
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );
        
        setCreationToggleBtn.addActionListener(e -> toggleListVisibility(setCreationToggleBtn));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(setCreationToggleBtn, BorderLayout.EAST);

        // Main content
        JPanel contentPanel = createPanel.panel(Color.WHITE, new GridBagLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        
        // Subject Label and Field
        JLabel subjectLabel = new JLabel("Subject Title");
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        gbc.insets = new Insets(0, 0, 5, 0);
        contentPanel.add(subjectLabel, gbc);

        JTextField subjectField = new JTextField();
        subjectField.setPreferredSize(new Dimension(0, 40));
        // Create bottom-only border with padding
        subjectField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x575757)), // Only bottom border
            BorderFactory.createEmptyBorder(5, 0, 5, 0)  // Top, left, bottom, right padding
        ));
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(subjectField, gbc);
        
        // Description Label and Area
        JLabel descriptionLabel = new JLabel("Description (Optional)");
        descriptionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        gbc.insets = new Insets(0, 0, 5, 0);
        contentPanel.add(descriptionLabel, gbc);
        
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(0, 300));
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(descriptionScroll, gbc);
        
        // Create Set Button
        JButton createSetBtn = createButton.button("Create Set", null, Color.WHITE, null, false);
        createSetBtn.setBackground(new Color(0x17BD0F));
        createSetBtn.setPreferredSize(new Dimension(100, 40));
        createSetBtn.addActionListener(e -> {
            String subject = subjectField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (subject.isEmpty()) {
                Toast.error("Please enter a subject title!");
                return;
            }           
            if (createNewSet(subject, description)) {
                subjectField.setText("");
                descriptionArea.setText("");
                cardLayout.show(mainCardPanel, "termsInput");
                refreshListContainer();
            }
        });

        JPanel buttonPanel = createPanel.panel(Color.WHITE, new FlowLayout(FlowLayout.LEFT, 0, 0), null);
        buttonPanel.add(createSetBtn);
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(buttonPanel, gbc);

        // Add glue to push everything to the top and fill remaining space
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(Box.createVerticalGlue(), gbc);
        
        // Add header and content to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private static JPanel createTermsInputPanel() {
        JPanel panel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        
        // Add empty border around the entire panel
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        termsInputTitleLabel = new JLabel("ADD TERMS TO NEW SET");
        termsInputTitleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));

        // Create buttons panel for the header right side
        JPanel headerButtonsPanel = createPanel.panel(Color.WHITE, new FlowLayout(FlowLayout.RIGHT, 10, 0), null);

        // Create Cancel button
        JButton cancelBtn = createButton.button("CANCEL", null, Color.WHITE, null, false);
        cancelBtn.setPreferredSize(new Dimension(130, 40));

        // Add hover effect for the cancel button
        new HoverButtonEffect(cancelBtn, 
            new Color(0xDC2626),  // default background (red)
            new Color(0xB91C1C),  // hover background (darker red)
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );

        // Create Save button
        JButton saveBtn = createButton.button("SAVE", null, Color.WHITE, null, false);
        saveBtn.setPreferredSize(new Dimension(130, 40));

        // Add hover effect for the save button
        new HoverButtonEffect(saveBtn, 
            new Color(0x17BD0F),  // default background (green)
            new Color(0x10B981),  // hover background (darker green)
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );

        // Add action listener for cancel button
        cancelBtn.addActionListener(e -> {
            // Clear any unsaved terms
            if (!temporaryTerms.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "You have unsaved terms. Are you sure you want to cancel?",
                    "Confirm Cancel",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                temporaryTerms.clear();
            }
            
            // Reset the panel state
            cardLayout.show(mainCardPanel, "setCreation");
            currentSetId = -1; // Reset current set
            currentSubject = ""; // Reset current subject
        });

        // Add action listener for save button (existing code)
        saveBtn.addActionListener(e -> {
            cardLayout.show(mainCardPanel, "setCreation");
            currentSetId = -1; // Reset current set
            currentSubject = ""; // Reset current subject
        });

        // Add buttons to the header buttons panel
        headerButtonsPanel.add(cancelBtn);
        headerButtonsPanel.add(saveBtn);

        // Add components to header
        headerPanel.add(termsInputTitleLabel, BorderLayout.WEST);
        headerPanel.add(headerButtonsPanel, BorderLayout.EAST);

        // Create main content panel
        JPanel contentPanel = createPanel.panel(Color.WHITE, new GridBagLayout(), null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Term section
        JLabel termLabel = new JLabel("Term");
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));

        JTextField termField = new JTextField();
        termField.setPreferredSize(new Dimension(0, 40));
        // Create bottom-only border with padding
        termField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x575757)), // Only bottom border
            BorderFactory.createEmptyBorder(5, 0, 5, 0)  // Top, left, bottom, right padding
        ));

        // Definition section
        JLabel definitionLabel = new JLabel("Definition");
        definitionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JTextArea definitionArea = new JTextArea();
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);
        JScrollPane definitionScroll = new JScrollPane(definitionArea);
        definitionScroll.setPreferredSize(new Dimension(0, 150));
        
        // Button panel
        JPanel buttonPanel = createPanel.panel(Color.WHITE, new FlowLayout(FlowLayout.RIGHT), null);
        
        JButton addTermBtn = createButton.button("Add Term", null, Color.WHITE, null, false);
        addTermBtn.setBackground(new Color(0x275CE2));
        addTermBtn.setPreferredSize(new Dimension(120, 40));
        
        // Add action listeners (keeping existing functionality)
        addTermBtn.addActionListener(e -> {
            String term = termField.getText().trim();
            String definition = definitionArea.getText().trim();
            
            if (term.isEmpty() || definition.isEmpty()) {
                Toast.error("Please enter both term and definition!");
                return;
            }
            
            if (addTermToSet(term, definition)) {
                termField.setText("");
                definitionArea.setText("");
                Toast.success("Term added successfully!");
            }
        });
        
        buttonPanel.add(addTermBtn);

        // Add components to content panel with GridBagLayout
        gbc.gridy = 0;
        contentPanel.add(termLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        contentPanel.add(termField, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        contentPanel.add(definitionLabel, gbc);
        
        gbc.gridy = 3;
        gbc.weighty = 1.0;  // Make definition area expand vertically
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 20, 10);
        contentPanel.add(definitionScroll, gbc);
        
        gbc.gridy = 4;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(buttonPanel, gbc);

        // Add header and content to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private static boolean insertQuiz(String term, String definition) {
        try {
            DatabaseManager.executeUpdate(
                "INSERT INTO quizzes (user_id, term, definition) VALUES (?, ?, ?)",
                UserSession.getUserId(), term, definition
            );
            showCenteredOptionPane(null, "Quiz added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshQuizContainer();
            refreshFlashcardMode();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCenteredOptionPane(null, "Error adding quiz: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void refreshQuizContainer() {
        if (quizContainer == null) return;
        quizContainer.removeAll();
        
        try {
            ResultSet rs = DatabaseManager.executeQuery(
                "SELECT term, definition FROM quizzes WHERE user_id = ?",
                UserSession.getUserId()
            );
            
            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                String term = rs.getString("term");
                String definition = rs.getString("definition");
                
                JPanel quizPanel = createQuizItemPanel(term, definition);
                quizContainer.add(quizPanel);
                quizContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            
            if (!hasItems) {
                JLabel noItemsLabel = new JLabel("No flashcards yet. Create one!");
                noItemsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                noItemsLabel.setForeground(new Color(0x707070));
                noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                quizContainer.add(noItemsLabel);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching flashcards: " + ex.getMessage());
        }

        quizContainer.revalidate();
        quizContainer.repaint();
        
        if (scrollPane != null) {
            scrollPane.getViewport().revalidate();
            scrollPane.getViewport().repaint();
        }
    }

    public static JPanel createListContainer() {
        // Main container with fixed width
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(700, 0));
        
        quizContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        refreshListContainer();
        
        JScrollPane scrollPane = new JScrollPane(quizContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // Make vertical scrollbar always visible
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // Keep horizontal scrollbar as never visible
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private static void refreshListContainer() {
        if (quizContainer == null) return;
        quizContainer.removeAll();
        
        try {
            String query = "SELECT set_id, subject, description FROM flashcard_sets WHERE user_id = ? ORDER BY subject ASC";
            ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());
            
            boolean hasItems = false;
            
            while (rs.next()) {
                hasItems = true;
                int setId = rs.getInt("set_id");
                String subject = rs.getString("subject");
                String description = rs.getString("description");
                
                JPanel setPanel = createSetItemPanel(setId, subject, description);
                quizContainer.add(setPanel);
                
                // Use consistent spacing of 5 pixels
                quizContainer.add(Box.createVerticalStrut(5));
            }
            
            // Add "No sets" message if needed
            if (!hasItems) {
                // Add empty state message
                JLabel noSetsLabel = new JLabel("No flashcard sets yet. Create one!", SwingConstants.CENTER);
                noSetsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
                noSetsLabel.setForeground(new Color(0x707070));
                noSetsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                quizContainer.add(noSetsLabel);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching flashcard sets: " + ex.getMessage());
        }
    }

    public static JPanel createSetItemPanel(int setId, String subject, String description) {
        // Main panel with fixed height and full width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        panel.setPreferredSize(new Dimension(550, 100));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 10));

        // Content panel
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(10, 0), null);

        // Text Panel (Left side)
        JPanel textPanel = createPanel.panel(null, new BorderLayout(), null);
        
        // Subject label at the top
        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        subjectLabel.setForeground(TEXT_COLOR);
        
        // Description label at the bottom
        JLabel descriptionLabel = new JLabel(description != null && !description.isEmpty() ? description : "No description");
        descriptionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        descriptionLabel.setForeground(new Color(0x666666));
        
        textPanel.add(subjectLabel, BorderLayout.NORTH);
        textPanel.add(descriptionLabel, BorderLayout.CENTER);

        // Button Panel (Right side)
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);

        // More button with popup menu
        JButton moreBtn = new JButton();
        try {
            ImageIcon moreIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\moreIconBlack.png");
            Image scaledImage = moreIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            moreBtn.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            moreBtn.setText("•••");
        }
        moreBtn.setBorderPainted(false);
        moreBtn.setContentAreaFilled(false);
        moreBtn.setFocusPainted(false);
        moreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        moreBtn.setPreferredSize(new Dimension(30, 30));

        // Popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Edit menu item with icon
        JMenuItem editItem = new JMenuItem();
        try {
            ImageIcon editIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\editIcon.png");
            Image scaledEditImage = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            editItem.setIcon(new ImageIcon(scaledEditImage));
        } catch (Exception e) {
            System.err.println("Failed to load edit icon: " + e.getMessage());
        }
        editItem.setText("Edit");

        // Add more terms menu item
        JMenuItem editSetsItem = new JMenuItem();
        try {
            ImageIcon editIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\editIcon.png");
            Image scaledEditImage = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            editSetsItem.setIcon(new ImageIcon(scaledEditImage));
        } catch (Exception e) {
            System.err.println("Failed to load edit icon: " + e.getMessage());
        }
        editSetsItem.setText("Add More Terms");

        // Delete menu item with icon
        JMenuItem deleteItem = new JMenuItem();
        try {
            ImageIcon deleteIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\deleteIcon.png");
            Image scaledDeleteImage = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            deleteItem.setIcon(new ImageIcon(scaledDeleteImage));
        } catch (Exception e) {
            System.err.println("Failed to load delete icon: " + e.getMessage());
        }
        deleteItem.setText("Delete");

        // Add items to popup menu (removed the startQuizItem)
        popupMenu.add(editItem);
        popupMenu.add(editSetsItem);
        popupMenu.add(deleteItem);

        // Add action listeners
        moreBtn.addActionListener(e -> {
            popupMenu.show(moreBtn, 0, moreBtn.getHeight());
        });

        // Existing edit functionality remains the same
        editItem.addActionListener(e -> {
            // Create input fields
            JTextField subjectField = new JTextField(subject);
            JTextArea descriptionArea = new JTextArea(description);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            descriptionArea.setRows(4); // Set preferred number of rows
            
            // Create main edit panel with vertical BoxLayout
            JPanel editPanel = new JPanel();
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            editPanel.setPreferredSize(new Dimension(400, 250)); // Set preferred size for the panel
            
            // Subject label
            JLabel subLabel = new JLabel("Subject");
            subLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 12));
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            editPanel.add(subLabel);
            
            // Subject field
            subjectField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            subjectField.setAlignmentX(Component.LEFT_ALIGNMENT);
            editPanel.add(subjectField);
            
            // Add space between fields
            editPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            // Description label
            JLabel descLabel = new JLabel("Description");
            descLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 12));
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            editPanel.add(descLabel);
            
            // Description text area with scroll pane
            JScrollPane scrollPane = new JScrollPane(descriptionArea);
            scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            editPanel.add(scrollPane);
            
            // Show dialog
            int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit Set", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String newSubject = subjectField.getText().trim();
                String newDescription = descriptionArea.getText().trim();
                
                if (newSubject.isEmpty()) {
                    Toast.error("Subject cannot be empty!");
                    return;
                }
                
                try {
                    DatabaseManager.executeUpdate(
                        "UPDATE flashcard_sets SET subject = ?, description = ? WHERE set_id = ? AND user_id = ?",
                        newSubject, newDescription, setId, UserSession.getUserId()
                    );
                    Toast.success("Set updated successfully!");
                    refreshListContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Toast.error("Error updating set: " + ex.getMessage());
                }
            }
        });

        // Add Edit Sets functionality (similar to Add More Terms)
        editSetsItem.addActionListener(e -> {
            // We can directly use the 'subject' parameter here
            termsInputTitleLabel.setText("ADD TERMS TO " + subject.toUpperCase());
            
            isCardView = false;
            currentCardIndex = 0;
            currentSetId = setId;
            cardLayout.show(mainCardPanel, "termsInput");
        });

        // Existing delete functionality remains the same
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete this set and all its flashcards?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = DatabaseManager.getConnection();
                    conn.setAutoCommit(false);
                    
                    try {
                        DatabaseManager.executeUpdate(
                            "DELETE FROM flashcards WHERE set_id = ? AND set_id IN " +
                            "(SELECT set_id FROM flashcard_sets WHERE user_id = ?)",
                            setId, UserSession.getUserId()
                        );
                        
                        DatabaseManager.executeUpdate(
                            "DELETE FROM flashcard_sets WHERE set_id = ? AND user_id = ?",
                            setId, UserSession.getUserId()
                        );
                        
                        conn.commit();
                        Toast.success("Set and all associated flashcards deleted successfully!");
                        refreshListContainer();
                    } catch (SQLException ex) {
                        conn.rollback();
                        throw ex;
                    } finally {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Toast.error("Error deleting set: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(moreBtn);
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Add hover effect
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);
        
        // Add click listener for showing flashcard mode
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Hide list container
                // isListVisible = false;
                // listContainer.setPreferredSize(new Dimension(0, 0));

                setCreationToggleBtn.setText("Show List");
            
                for (JButton button : toggleButtons) {
                    button.setText("Show List");
                }
                
                // Store the state for when user returns via back button
                // setCreationToggleBtn.setText("Hide List");
                
                listContainer.revalidate();
                listContainer.repaint();
                
                // Show flashcard mode
                showFlashcardMode(setId);
            }
        });
        
        return panel;
    }

    public static void showFlashcardMode(int setId) {
        // Update flashcard mode panel with the selected set's cards
        JPanel flashcardModePanel = createFlashcardModePanel(setId);
        mainCardPanel.add(flashcardModePanel, "flashcardMode");
        cardLayout.show(mainCardPanel, "flashcardMode");
    }

    private static JPanel createQuizItemPanel(String term, String definition) {
        // Main panel with fixed height and flexible width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 60));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // This ensures proper width scaling
        
        // Inner panel for consistent padding
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 70));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Text panel for term and definition
        JPanel textPanel = createPanel.panel(LIST_ITEM_COLOR, new GridLayout(2, 1, 2, 2), null);
        
        JLabel termLabel = new JLabel(term);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        termLabel.setForeground(TEXT_COLOR);
        
        JLabel definitionLabel = new JLabel(definition);
        definitionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        definitionLabel.setForeground(new Color(0x666666));
        
        textPanel.add(termLabel);
        textPanel.add(definitionLabel);
        
        // Button panel
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);
        
        // Edit button
        JButton editBtn = createButton.button("Edit", new Color(0xE9E9E9), new Color(0x242424), null, false);
        editBtn.setPreferredSize(new Dimension(70, 40));
        new HoverButtonEffect(editBtn, 
            new Color(0xE9E9E9), // default background
            new Color(0xBFBFBF), // hover background
            new Color(0x242424), // default text
            Color.WHITE         // hover text
        );

        // Delete button
        JButton deleteBtn = createButton.button("Delete", new Color(0xFB2C36), Color.WHITE, null, false);
        deleteBtn.setPreferredSize(new Dimension(70, 40));
        new HoverButtonEffect(deleteBtn, 
            new Color(0xFB2C36),  // default background
            new Color(0xFF6467),  // hover background
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );

        editBtn.addActionListener(e -> {
            JTextField termField = new JTextField(term);
            JTextField definitionField = new JTextField(definition);
            
            JPanel editPanel = new JPanel(new GridLayout(4, 1));  // Changed from 'panel' to 'editPanel'
            editPanel.add(new JLabel("Edit term:"));
            editPanel.add(termField);
            editPanel.add(new JLabel("Edit definition:"));
            editPanel.add(definitionField);
            
            JOptionPane pane = new JOptionPane(editPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = pane.createDialog(null, "Edit Flashcard");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            
            Object result = pane.getValue();
            if (result != null && (Integer) result == JOptionPane.OK_OPTION) {
                String newTerm = termField.getText().trim();
                String newDefinition = definitionField.getText().trim();
                
                if (!newTerm.isEmpty() && !newDefinition.isEmpty()) {
                    updateFlashcard(term, newTerm, newDefinition);
                }
            }
        });
        
        deleteBtn.addActionListener(e -> {
            JOptionPane pane = new JOptionPane(
                "Are you sure you want to delete this flashcard?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
            );
            JDialog dialog = pane.createDialog(null, "Confirm Delete");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            
            Object result = pane.getValue();
            if (result != null && (Integer) result == JOptionPane.YES_OPTION) {
                deleteFlashcard(term);
            }
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Add hover effect
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);

        return panel;
    }
   
    private static JPanel createFlashcardModePanel(int setId) {
        JPanel mainContainer = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createPanel.panel(null, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Left side panel for buttons
        JPanel leftButtonsPanel = createPanel.panel(null, new FlowLayout(FlowLayout.LEFT, 10, 0), null);
        
        // Back button
        JButton backButton = createButton.button("←", null, Color.WHITE, null, false);
        backButton.setBackground(new Color(0x0065D9));
        backButton.setPreferredSize(new Dimension(70, 40));
        backButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        backButton.addActionListener(e -> {
            isCardView = false;
            currentCardIndex = 0;
            isListVisible = true;  // Reset to true when going back
            listContainer.setPreferredSize(new Dimension(600, 0));
            
            // Update all toggle buttons to "Hide List"
            for (JButton button : toggleButtons) {
                button.setText("Show List");
            }
            
            cardLayout.show(mainCardPanel, "setCreation");
        });
        
        // Toggle list visibility button
        JButton toggleListBtn = createButton.button("Hide List", null, Color.WHITE, null, false);
        toggleListBtn.setBackground(new Color(0x275CE2));
        toggleListBtn.setPreferredSize(new Dimension(120, 40));
        toggleListBtn.addActionListener(e -> toggleListVisibility(toggleListBtn));
        toggleButtons.add(toggleListBtn);
        
        // Add More Terms button
        addMoreTermsBtn = createButton.button("Add Terms", null, Color.WHITE, null, false);
        addMoreTermsBtn.setBackground(new Color(0x275CE2));
        addMoreTermsBtn.setPreferredSize(new Dimension(120, 40));
        addMoreTermsBtn.addActionListener(e -> {
            // Set the title for the terms input panel
            String subject = "";
            try {
                String query = "SELECT subject FROM flashcard_sets WHERE set_id = ?";
                ResultSet rs = DatabaseManager.executeQuery(query, setId);
                if (rs.next()) {
                    subject = rs.getString("subject");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            termsInputTitleLabel.setText("ADD TERMS TO " + subject.toUpperCase());
            
            isCardView = false;
            currentCardIndex = 0;
            currentSetId = setId;
            cardLayout.show(mainCardPanel, "termsInput");
        });

        // View Toggle button
        viewToggleBtn = createButton.button(isCardView ? "List View" : "Card View", null, Color.WHITE, null, false);
        viewToggleBtn.setBackground(new Color(0x275CE2));
        viewToggleBtn.setPreferredSize(new Dimension(120, 40));
        viewToggleBtn.addActionListener(e -> {
            isCardView = !isCardView;
            viewToggleBtn.setText(isCardView ? "List View" : "Card View");
            refreshFlashcardView(mainContainer, setId);
        });
        
        leftButtonsPanel.add(backButton);
        leftButtonsPanel.add(toggleListBtn);
        leftButtonsPanel.add(addMoreTermsBtn);
        leftButtonsPanel.add(viewToggleBtn);
        
        // Add panels to header
        headerPanel.add(leftButtonsPanel, BorderLayout.WEST);
        
        // Fetch flashcards data
        flashcardsList.clear();
        try {
            String query = "SELECT term, definition FROM flashcards WHERE set_id = ?";
            ResultSet rs = DatabaseManager.executeQuery(query, setId);
            
            while (rs.next()) {
                Map<String, String> card = new HashMap<>();
                card.put("term", rs.getString("term"));
                card.put("definition", rs.getString("definition"));
                flashcardsList.add(card);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error loading flashcards: " + ex.getMessage());
        }

        // Add content based on view mode
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        refreshFlashcardView(mainContainer, setId);
        
        return mainContainer;
    }

    private static void refreshFlashcardView(JPanel mainContainer, int setId) {
        // Remove existing content panel if it exists
        Component[] components = mainContainer.getComponents();
        for (Component comp : components) {
            if (comp != mainContainer.getComponent(0)) { // Keep header panel
                mainContainer.remove(comp);
            }
        }

        if (isCardView) {
            mainContainer.add(createCardView(), BorderLayout.CENTER);
        } else {
            JPanel cardsContainer = createListView(setId);
            JScrollPane scrollPane = new JScrollPane(cardsContainer);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            mainContainer.add(scrollPane, BorderLayout.CENTER);
        }

        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private static JPanel createCardView() {
        JPanel cardViewPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        
        if (flashcardsList.isEmpty()) {
            JLabel noCardsLabel = new JLabel("No flashcards in this set yet!", SwingConstants.CENTER);
            noCardsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
            noCardsLabel.setForeground(new Color(0x707070));
            cardViewPanel.add(noCardsLabel, BorderLayout.CENTER);
            return cardViewPanel;
        }

        // Create card display panel
        JPanel cardDisplay = createPanel.panel(new Color(0xF5F5F5), new GridBagLayout(), new Dimension(600, 400));
        cardDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Center the card display in the view
        JPanel centeringPanel = createPanel.panel(Color.WHITE, new GridBagLayout(), null);
        centeringPanel.add(cardDisplay);

        // Add current card content
        Map<String, String> currentCard = flashcardsList.get(currentCardIndex);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        
        // Term Label at the top
        JLabel termLabel = new JLabel(currentCard.get("term"), SwingConstants.CENTER);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        gbc.weighty = 0.2;
        gbc.gridy = 0;
        cardDisplay.add(termLabel, gbc);
        
        // Definition in the center
        JTextArea definitionArea = new JTextArea(currentCard.get("definition"));
        definitionArea.setFont(new Font("Segoe UI Variable", Font.PLAIN, 18));
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);
        definitionArea.setEditable(false);
        definitionArea.setBackground(cardDisplay.getBackground());
        definitionArea.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Add definition with more vertical weight to center it
        gbc.weighty = 1.0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 0, 20, 0);
        cardDisplay.add(definitionArea, gbc);

        // Navigation buttons
        JPanel navigationPanel = createPanel.panel(Color.WHITE, new FlowLayout(FlowLayout.CENTER, 20, 10), null);
        
        JButton prevButton = createButton.button("Previous", null, Color.WHITE, null, false);
        prevButton.setBackground(new Color(0x275CE2));
        prevButton.setPreferredSize(new Dimension(100, 40));
        prevButton.addActionListener(e -> navigateCards(-1));
        prevButton.setEnabled(currentCardIndex > 0);

        JLabel cardCounter = new JLabel(String.format("%d/%d", currentCardIndex + 1, flashcardsList.size()));
        cardCounter.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JButton nextButton = createButton.button("Next", null, Color.WHITE, null, false);
        nextButton.setBackground(new Color(0x275CE2));
        nextButton.setPreferredSize(new Dimension(100, 40));
        nextButton.addActionListener(e -> navigateCards(1));
        nextButton.setEnabled(currentCardIndex < flashcardsList.size() - 1);

        navigationPanel.add(prevButton);
        navigationPanel.add(cardCounter);
        navigationPanel.add(nextButton);

        cardViewPanel.add(centeringPanel, BorderLayout.CENTER);
        cardViewPanel.add(navigationPanel, BorderLayout.SOUTH);

        // Add key listener for navigation
        cardViewPanel.setFocusable(true);
        cardViewPanel.requestFocusInWindow();
        cardViewPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT && currentCardIndex > 0) {
                    navigateCards(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentCardIndex < flashcardsList.size() - 1) {
                    navigateCards(1);
                }
            }
        });

        return cardViewPanel;
    }

    private static void navigateCards(int direction) {
        currentCardIndex += direction;
        if (currentCardIndex < 0) currentCardIndex = 0;
        if (currentCardIndex >= flashcardsList.size()) currentCardIndex = flashcardsList.size() - 1;
        
        // Find the main container and refresh the view
        Component[] components = mainCardPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.isVisible()) {
                refreshFlashcardView((JPanel)comp, currentSetId);
                break;
            }
        }
    }

    private static JPanel createListView(int setId) {
        JPanel cardsContainer = createPanel.panel(Color.WHITE, null, null);
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        
        if (flashcardsList.isEmpty()) {
            JLabel noCardsLabel = new JLabel("No flashcards in this set yet!", SwingConstants.CENTER);
            noCardsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
            noCardsLabel.setForeground(new Color(0x707070));
            noCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardsContainer.add(noCardsLabel);
            return cardsContainer;
        }

        for (Map<String, String> card : flashcardsList) {
            JPanel flashcard = createFlashcardItem(card.get("term"), card.get("definition"));
            cardsContainer.add(flashcard);
            cardsContainer.add(Box.createVerticalStrut(5)); // Changed from 10 to 5
        }

        return cardsContainer;
    }

    // Helper method to create individual flashcard items
    private static JPanel createFlashcardItem(String term, String definition) {
        JPanel panel = createPanel.panel(new Color(0xF5F5F5), new BorderLayout(), null);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Content Panel
        JPanel contentPanel = createPanel.panel(panel.getBackground(), new BorderLayout(10, 0), null);
        
        // Term and Definition Panel
        JPanel textPanel = createPanel.panel(panel.getBackground(), new BorderLayout(0, 10), null);
        
        // Term panel with More button
        JPanel termPanel = createPanel.panel(panel.getBackground(), new BorderLayout(10, 0), null);
        JLabel termLabel = new JLabel(term);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        // Definition area
        JTextArea definitionArea = new JTextArea(definition);
        definitionArea.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);
        definitionArea.setEditable(false);
        definitionArea.setBackground(panel.getBackground());
        
        JScrollPane definitionScroll = new JScrollPane(definitionArea);
        definitionScroll.setBorder(null);
        definitionScroll.setBackground(panel.getBackground());
        
        // More button
        JButton moreBtn = new JButton();
        try {
            ImageIcon moreIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\moreIconBlack.png");
            Image scaledImage = moreIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            moreBtn.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            moreBtn.setText("•••");
        }
        moreBtn.setBorderPainted(false);
        moreBtn.setContentAreaFilled(false);
        moreBtn.setFocusPainted(false);
        moreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        moreBtn.setPreferredSize(new Dimension(30, 30));

        // Popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Edit menu item
        JMenuItem editItem = new JMenuItem("Edit");
        try {
            ImageIcon editIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\editIcon.png");
            Image scaledEditImage = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            editItem.setIcon(new ImageIcon(scaledEditImage));
        } catch (Exception e) {
            System.err.println("Failed to load edit icon: " + e.getMessage());
        }
        
        // Delete menu item
        JMenuItem deleteItem = new JMenuItem("Delete");
        try {
            ImageIcon deleteIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\deleteIcon.png");
            Image scaledDeleteImage = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            deleteItem.setIcon(new ImageIcon(scaledDeleteImage));
        } catch (Exception e) {
            System.err.println("Failed to load delete icon: " + e.getMessage());
        }

        // Add action listeners
        editItem.addActionListener(e -> {
            JTextField termField = new JTextField(term);
            JTextArea definitionField = new JTextArea(definition);
            definitionField.setLineWrap(true);
            definitionField.setWrapStyleWord(true);
            
            JPanel editPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            editPanel.add(new JLabel("Edit term:"));
            editPanel.add(termField);
            editPanel.add(new JLabel("Edit definition:"));
            
            JScrollPane scrollPane = new JScrollPane(definitionField);
            scrollPane.setPreferredSize(new Dimension(300, 100));
            editPanel.add(scrollPane);
            
            int result = JOptionPane.showConfirmDialog(
                null, 
                editPanel, 
                "Edit Flashcard",
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String newTerm = termField.getText().trim();
                String newDefinition = definitionField.getText().trim();
                
                if (!newTerm.isEmpty() && !newDefinition.isEmpty()) {
                    try {
                        DatabaseManager.executeUpdate(
                            "UPDATE quizzes SET term = ?, definition = ? WHERE term = ? AND user_id = ?",
                            newTerm, newDefinition, term, UserSession.getUserId()
                        );
                        
                        // Update the UI components directly
                        termLabel.setText(newTerm);
                        definitionArea.setText(newDefinition);
                        
                        // Update the panel's layout
                        panel.revalidate();
                        panel.repaint();
                        
                        Toast.success("Flashcard updated successfully!");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        Toast.error("Error updating flashcard: " + ex.getMessage());
                    }
                } else {
                    Toast.error("Both term and definition are required!");
                }
            }
        });
        
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete this flashcard?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // First check if the flashcard exists for this user
                    String checkQuery = "SELECT COUNT(*) FROM flashcards WHERE term = ? AND set_id IN " +
                                      "(SELECT set_id FROM flashcard_sets WHERE user_id = ?)";
                    ResultSet rs = DatabaseManager.executeQuery(checkQuery, term, UserSession.getUserId());
                    
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Delete the flashcard
                        String deleteQuery = "DELETE FROM flashcards WHERE term = ? AND set_id IN " +
                                          "(SELECT set_id FROM flashcard_sets WHERE user_id = ?)";
                        DatabaseManager.executeUpdate(deleteQuery, term, UserSession.getUserId());
                        
                        // Remove the panel from its parent container
                        Container parent = panel.getParent();
                        if (parent != null) {
                            parent.remove(panel);
                            parent.revalidate();
                            parent.repaint();
                        }
                        
                        Toast.success("Flashcard deleted successfully!");
                        
                        // Update the flashcards list
                        flashcardsList.removeIf(card -> card.get("term").equals(term));
                        
                        // If in card view mode, navigate to the previous card if necessary
                        if (isCardView && !flashcardsList.isEmpty()) {
                            if (currentCardIndex >= flashcardsList.size()) {
                                currentCardIndex = flashcardsList.size() - 1;
                            }
                            refreshFlashcardView((JPanel)parent.getParent(), currentSetId);
                        }
                    } else {
                        Toast.error("Flashcard not found or you don't have permission to delete it.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Toast.error("Error deleting flashcard: " + ex.getMessage());
                }
            }
        });

        // Add items to popup menu
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        // Add action listener to more button
        moreBtn.addActionListener(e -> {
            popupMenu.show(moreBtn, 0, moreBtn.getHeight());
        });
        
        // Add components to term panel
        termPanel.add(termLabel, BorderLayout.CENTER);
        termPanel.add(moreBtn, BorderLayout.EAST);
        
        // Add components to text panel
        textPanel.add(termPanel, BorderLayout.NORTH);
        textPanel.add(definitionScroll, BorderLayout.CENTER);
        
        // Add text panel to content panel
        contentPanel.add(textPanel, BorderLayout.CENTER);
        
        // Add content panel to main panel
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Set preferred size
        panel.setPreferredSize(new Dimension(600, 150));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        return panel;
    }

    // Add this method to refresh the view mode
    public static void refreshFlashcardMode() {
        if (mainCardPanel != null) {
            mainCardPanel.remove(mainCardPanel.getComponent(1)); // Remove old flashcard mode panel
            mainCardPanel.add(createFlashcardModePanel(currentSetId), "flashcardMode"); // Pass currentSetId
            mainCardPanel.revalidate();
            mainCardPanel.repaint();
        }
    }

    private static void updateFlashcard(String oldTerm, String newTerm, String newDefinition) {
        try {
            DatabaseManager.executeUpdate(
                "UPDATE quizzes SET term = ?, definition = ? WHERE term = ? AND user_id = ?",
                newTerm, newDefinition, oldTerm, UserSession.getUserId()
            );
            Toast.success("Flashcard updated successfully!");
            refreshQuizContainer();
            refreshFlashcardMode();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error updating flashcard: " + ex.getMessage());
        }
    }

    private static void deleteFlashcard(String term) {
        try {
            DatabaseManager.executeUpdate(
                "DELETE FROM quizzes WHERE term = ? AND user_id = ?",
                term, UserSession.getUserId()
            );
            Toast.success("Flashcard deleted successfully!");
            refreshQuizContainer();
            refreshFlashcardMode();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error deleting flashcard: " + ex.getMessage());
        }
    }

    private static void showCenteredOptionPane(Component parentComponent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void addToTemporaryList(String subject, String term, String definition) {
        Map<String, String> termMap = new HashMap<>();
        termMap.put("term", term);
        termMap.put("definition", definition);
        temporaryTerms.add(termMap);
    }
    
    private static void clearTemporaryList() {
        temporaryTerms.clear();
    }
    
    private static boolean createNewSet(String subject, String description) {
        currentSubject = subject; 
        try {
            String setQuery = "INSERT INTO flashcard_sets (user_id, subject, description) VALUES (?, ?, ?)";
            int setId;
            
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(setQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, UserSession.getUserId());
                stmt.setString(2, subject);
                stmt.setString(3, description);
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        setId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get generated set ID");
                    }
                }
            }
            
            // Then add all terms from temporary storage
            String cardQuery = "INSERT INTO flashcards (set_id, term, definition) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(cardQuery)) {
                for (Map<String, String> term : temporaryTerms) {
                    stmt.setInt(1, setId);
                    stmt.setString(2, term.get("term"));
                    stmt.setString(3, term.get("definition"));
                    stmt.executeUpdate();
                }
            }
            
            // Clear temporary storage after successful creation
            temporaryTerms.clear();
            currentSetId = setId;  // Set the current set ID
            
            Toast.success("Flashcard set created successfully!");
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error creating flashcard set: " + ex.getMessage());
            return false;
        }
    }

    private static boolean addTermToSet(String term, String definition) {
        if (currentSetId == -1) {
            // If no set is selected, add to temporary storage
            Map<String, String> termMap = new HashMap<>();
            termMap.put("term", term);
            termMap.put("definition", definition);
            temporaryTerms.add(termMap);
            return true;
        } else {
            // If a set is selected, add directly to database
            try {
                String query = "INSERT INTO flashcards (set_id, term, definition) VALUES (?, ?, ?)";
                DatabaseManager.executeUpdate(query, currentSetId, term, definition);
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                Toast.error("Error adding term: " + ex.getMessage());
                return false;
            }
        }
    }
    private static void toggleListVisibility(JButton sourceButton) {
        if (listContainer != null) {
            isListVisible = !isListVisible;
            String buttonText = isListVisible ? "Hide List" : "Show List";
            int listWidth = isListVisible ? 700 : 0;
            
            // Update both buttons if they exist
            if (setCreationToggleBtn != null) {
                setCreationToggleBtn.setText(buttonText);
            }
            
            // Update the source button (if it's different from setCreationToggleBtn)
            if (sourceButton != setCreationToggleBtn) {
                sourceButton.setText(buttonText);
            }
            
            // Update list container visibility
            listContainer.setPreferredSize(new Dimension(listWidth, 0));
            listContainer.revalidate();
            listContainer.repaint();
            
            // Revalidate parent containers
            Container parent = listContainer.getParent();
            while (parent != null) {
                parent.revalidate();
                parent.repaint();
                parent = parent.getParent();
            }
        }
    }

    private static void showQuizTypeDialog(int setId, String subject) {
        // First, get the total number of flashcards
        int totalFlashcards = getTotalFlashcards(setId);
        if (totalFlashcards == 0) {
            Toast.error("No flashcards found in this set!");
            return;
        }

        // Create dialog components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Quiz type selection
        JLabel typeLabel = new JLabel("Quiz Type:");
        String[] quizTypes = {"Multiple Choice", "Identification"};
        JComboBox<String> quizTypeCombo = new JComboBox<>(quizTypes);

        // Number of items selection
        JLabel itemsLabel = new JLabel("Number of Items:");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
            Math.min(10, totalFlashcards), // initial value
            1,                            // minimum value
            totalFlashcards,              // maximum value
            1                            // step
        );
        JSpinner itemsSpinner = new JSpinner(spinnerModel);

        // Time duration selection
        JLabel timeLabel = new JLabel("Time Duration (minutes):");
        Integer[] timeDurations = {5, 10, 15, 20, 30, 45, 60};
        JComboBox<Integer> timeCombo = new JComboBox<>(timeDurations);
        timeCombo.setSelectedItem(30); // Default 30 minutes

        // Add components to panel
        gbc.gridy = 0;
        panel.add(typeLabel, gbc);
        gbc.gridy = 1;
        panel.add(quizTypeCombo, gbc);
        gbc.gridy = 2;
        panel.add(itemsLabel, gbc);
        gbc.gridy = 3;
        panel.add(itemsSpinner, gbc);
        gbc.gridy = 4;
        panel.add(timeLabel, gbc);
        gbc.gridy = 5;
        panel.add(timeCombo, gbc);

        int result = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Quiz Settings",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedType = (String) quizTypeCombo.getSelectedItem();
            int numberOfItems = (Integer) itemsSpinner.getValue();
            int timeDuration = (Integer) timeCombo.getSelectedItem();

            // Switch to QuizzerPanel and start the quiz
            StudyPanel.showQuizzer();
            if ("Identification".equals(selectedType)) {
                QuizzerPanel.startIdentificationQuiz(setId, subject, numberOfItems, timeDuration);
            } else {
                QuizzerPanel.startMultipleChoiceQuiz(setId, subject, numberOfItems, timeDuration);
            }
        }
    }

    private static int getTotalFlashcards(int setId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT COUNT(*) FROM flashcards WHERE set_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, setId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error counting flashcards: " + ex.getMessage());
        }
        return 0;
    }

    // Add this getter method to access the quiz container
    public static JPanel getQuizContainer() {
        return quizContainer;
    }
}
