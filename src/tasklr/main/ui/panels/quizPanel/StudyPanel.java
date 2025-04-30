package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.utilities.*;
import java.awt.*;

public class StudyPanel {
    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Add this constant for header color
    private static final int HEADER_HEIGHT = 70;                       // Add this constant for header height

    public static JPanel createStudyPanel() {
        JPanel mainPanel = createPanel.panel(new Color(0xFFFFFF), new BorderLayout(), new Dimension(100, 100));
        Border mainBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D)),
        BorderFactory.createEmptyBorder(20, 20, 0, 20)  // Add 20px padding on all sides
    );
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
        JPanel navPanel = createPanel.panel(PRIMARY_COLOR, new BorderLayout(), new Dimension(0, HEADER_HEIGHT));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel navButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        navButtonsPanel.setOpaque(false);

        // Create navigation buttons with initial states
        JButton flashcardBtn = createButton.button("FLASHCARDS", null, PRIMARY_COLOR, null, false);
        flashcardBtn.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        flashcardBtn.setPreferredSize(new Dimension(180, 40));
        flashcardBtn.setBorderPainted(false);
        flashcardBtn.setFocusPainted(false);
        
        JButton quizzerBtn = createButton.button("QUIZZER", null, PRIMARY_COLOR, null, false);
        quizzerBtn.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        quizzerBtn.setPreferredSize(new Dimension(180, 40));
        quizzerBtn.setBorderPainted(false);
        quizzerBtn.setFocusPainted(false);

        flashcardBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "flashcard");
        });

        quizzerBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "quizzer");
        });

        // Set initial active state (Flashcard active by default)
        flashcardBtn.setBackground(PRIMARY_COLOR);
        flashcardBtn.setForeground(Color.WHITE);
        quizzerBtn.setBackground(PRIMARY_COLOR);
        quizzerBtn.setForeground(Color.WHITE);

        navButtonsPanel.add(flashcardBtn);
        navButtonsPanel.add(quizzerBtn);
        navPanel.add(navButtonsPanel, BorderLayout.WEST);

        return navPanel;
    }

    // Update the static methods to handle button states instead of navLabel
    public static void showFlashcardCreation() {
        if (cardPanel != null && cardLayout != null) {
            cardLayout.show(cardPanel, "flashcard");
        }
    }

    public static void showQuizzer() {
        if (cardPanel != null && cardLayout != null) {
            cardLayout.show(cardPanel, "quizzer");
        }
    }
}
