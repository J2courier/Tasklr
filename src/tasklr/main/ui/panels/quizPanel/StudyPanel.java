package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.utilities.createButton;
import tasklr.utilities.*;
import java.awt.*;

public class StudyPanel {
    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Add this constant for header color
    private static final int HEADER_HEIGHT = 70;                       // Add this constant for header height
    private static JLabel navLabel;

    public static JPanel createStudyPanel() {
        JPanel mainPanel = createPanel.panel(new Color(0xFFFFFF), new BorderLayout(), new Dimension(100, 100));
        Border mainBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        mainPanel.setBorder(mainBorder);

        // Create navigation panel
        JPanel navPanel = createNavPanel();
        mainPanel.add(navPanel, BorderLayout.NORTH);

        // Create card panel
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Add panels to card layout
        cardPanel.add(FlashcardPanel.createFlashcardPanel(), "flashcard");
        cardPanel.add(QuizzerPanel.createQuizzerPanel(), "quizzer");

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private static JPanel createNavPanel() {
        // Change background color to PRIMARY_COLOR and set fixed height
        JPanel navPanel = createPanel.panel(PRIMARY_COLOR, new BorderLayout(), new Dimension(0, HEADER_HEIGHT));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Left side of header with title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);  // Make transparent to show header background
        
        // Update navLabel style to match other headers
        navLabel = new JLabel("FLASHCARDS");
        navLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        navLabel.setForeground(Color.WHITE);  // Change text color to white
        titlePanel.add(navLabel);

        // Right side of header with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setOpaque(false);  // Make transparent to show header background

        // Update button styles
        JButton flashcardBtn = createButton.button("Flashcards", PRIMARY_COLOR, Color.WHITE, null, false);
        flashcardBtn.setPreferredSize(new Dimension(120, 40));
        
        JButton quizzerBtn = createButton.button("Take a Quiz", PRIMARY_COLOR, Color.WHITE, null, false);
        quizzerBtn.setPreferredSize(new Dimension(120, 40));

        // Add hover effects to buttons
        new HoverButtonEffect(flashcardBtn, 
            new Color(PRIMARY_COLOR.getRGB()),  // default background
            new Color(0x153C9B),  // hover background
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );

        new HoverButtonEffect(quizzerBtn, 
            new Color(PRIMARY_COLOR.getRGB()),  // default background
            new Color(0x153C9B),  // hover background
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );

        flashcardBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "flashcard");
            navLabel.setText("FLASHCARDS");
        });

        quizzerBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "quizzer");
            navLabel.setText("QUIZZER");
        });

        buttonPanel.add(flashcardBtn);
        buttonPanel.add(quizzerBtn);

        // Add components to navPanel
        navPanel.add(titlePanel, BorderLayout.WEST);
        navPanel.add(buttonPanel, BorderLayout.EAST);

        return navPanel;
    }

    // Add these new static methods for external control
    public static void showFlashcardCreation() {
        if (cardPanel != null && cardLayout != null) {
            cardLayout.show(cardPanel, "flashcard");
            if (navLabel != null) {
                navLabel.setText("Flashcards");
            }
        }
    }

    public static void showQuizzer() {
        if (cardPanel != null && cardLayout != null) {
            cardLayout.show(cardPanel, "quizzer");
            if (navLabel != null) {
                navLabel.setText("Quizzer");
            }
        }
    }
}
