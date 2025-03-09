package tasklr.main.quizPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;
import java.awt.*;

public class StudyPanel {
    private static CardLayout cardLayout;
    private static JPanel cardPanel;

    public static JPanel createStudyPanel(String username) {
        JPanel mainPanel = createPanel.panel(new Color(0xf1f3f6), new BorderLayout(), new Dimension(100, 100));
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
        cardPanel.add(FlashcardPanel.createFlashcardPanel(username), "flashcard");
        cardPanel.add(QuizzerPanel.createQuizzerPanel(username), "quizzer");

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private static JPanel createNavPanel() {
        JPanel navPanel = createPanel.panel(new Color(0xFFFFFF), new FlowLayout(FlowLayout.LEFT), new Dimension(0, 50));
        Border navBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x749AAD));
        navPanel.setBorder(navBorder);

        JButton flashcardBtn = createButton.button("Flashcards", null, new Color(0x0065D9),null, false);
        JButton quizzerBtn = createButton.button("Quizzer", null, new Color(0x0065D9), null, false);

        flashcardBtn.addActionListener(e -> cardLayout.show(cardPanel, "flashcard"));
        quizzerBtn.addActionListener(e -> cardLayout.show(cardPanel, "quizzer"));

        navPanel.add(flashcardBtn);
        navPanel.add(quizzerBtn);

        return navPanel;
    }
}
