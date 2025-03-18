package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;
import java.awt.*;

public class StudyPanel {
    private static CardLayout cardLayout;
    private static JPanel cardPanel;
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    private static JLabel navLabel; // Add this field to store the label reference

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
        JPanel navPanel = createPanel.panel(BACKGROUND_COLOR, new FlowLayout(FlowLayout.LEFT), new Dimension(0, 60));
        Border navBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E3E2));
        navPanel.setBorder(navBorder);

        // Initialize navLabel with default text "Flashcards"
        navLabel = new JLabel("Flashcards");
        navLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        navLabel.setForeground(new Color(0x1d1d1d));
        navLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 280));
        navPanel.add(navLabel);

        JButton flashcardBtn = createButton.button("Flashcards", new Color(0x0082FC), Color.WHITE, null, false);
        flashcardBtn.setPreferredSize(new Dimension(100, 40));
        JButton quizzerBtn = createButton.button("Quizzer", new Color(0x0082FC), Color.WHITE, null, false);
        quizzerBtn.setPreferredSize(new Dimension(100, 40));

        // Modify the action listeners to update the navLabel text
        flashcardBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "flashcard");
            navLabel.setText("Flashcards");
        });

        quizzerBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "quizzer");
            navLabel.setText("Quizzer");
        });

        navPanel.add(flashcardBtn);
        navPanel.add(quizzerBtn);

        return navPanel;
    }
}