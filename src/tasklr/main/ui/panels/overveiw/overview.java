package tasklr.main.ui.panels.overveiw;

import javax.swing.*;
import java.awt.*;
import tasklr.main.ui.components.ComponentFactory;
import tasklr.main.ui.components.TaskCounterPanel;
import tasklr.utilities.RefreshUI;
import tasklr.utilities.createPanel;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;
import tasklr.main.ui.panels.quizPanel.StudyPanel;
import tasklr.utilities.DatabaseManager;
import tasklr.authentication.UserSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import tasklr.utilities.UIRefreshManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class overview {
    // Color constants
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Primary blue
    private static final Color SECONDARY_COLOR = new Color(0xE0E3E2);  // Light gray
    private static final Color BACKGROUND_COLOR = Color.WHITE; // Light background
    private static final Color CARD_COLOR = new Color(0xFFFFFF);       // White
    private static final Color TEXT_DARK = new Color(0x1D1D1D);        // Dark text
    private static final Color BORDER_COLOR = new Color(0xE0E0E0);     // Border gray

    // New colors for bars
    private static final Color PENDING_COLOR = new Color(0xF87171);    // Red for pending
    private static final Color COMPLETED_COLOR = new Color(0x7CCE00);  // Green for completed
    private static final Color TOTAL_COLOR = new Color(0x275CE2);      // Blue for total

    // New colors for quiz statistics
    private static final Color TOTAL_ITEMS_COLOR = new Color(0x3498DB);    // Blue
    private static final Color AVG_SCORE_COLOR = new Color(0xF1C40F);      // Yellow
    private static final Color CORRECT_COLOR = new Color(0x2ECC71);        // Green
    private static final Color INCORRECT_COLOR = new Color(0xE74C3C);      // Red

    private static TaskCounterPanel pendingTasksPanel;
    private static TaskCounterPanel completedTasksPanel;
    private static TaskCounterPanel totalTasksPanel;
    
    // Add new panel variables for flashcard statistics
    private static TaskCounterPanel totalFlashcardSetsPanel;
    private static TaskCounterPanel pendingQuizProgressPanel;
    private static TaskCounterPanel completedQuizProgressPanel;
    

    private static CustomBarGraph taskBarGraph;
    private static CustomBarGraph quizStatsBarGraph;
    
    // Add UIRefreshManager instance
    private static UIRefreshManager refreshManager;
    private static final int REFRESH_INTERVAL = 2000;

    // Custom Bar Graph Component
    static class CustomBarGraph extends JPanel {
        private Map<String, Integer> data;
        private final int BAR_WIDTH = 60;
        private final int BAR_SPACING = 40;
        private final int ANIMATION_DURATION = 500; // milliseconds
        private final Map<String, Integer> currentHeights;
        private Timer animationTimer;

        public CustomBarGraph() {
            setBackground(CARD_COLOR);
            data = new HashMap<>();
            currentHeights = new HashMap<>();
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setPreferredSize(new Dimension(0, 300));
        }

        public void updateData(Map<String, Integer> newData) {
            if (this.data.equals(newData)) return;
            
            // Initialize current heights if needed
            for (String key : newData.keySet()) {
                if (!currentHeights.containsKey(key)) {
                    currentHeights.put(key, 0);
                }
            }

            // Stop existing animation if running
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }

            Map<String, Integer> targetHeights = new HashMap<>();
            int maxValue = newData.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            int maxHeight = getHeight() - 100; // Leave space for labels

            // Calculate target heights
            for (Map.Entry<String, Integer> entry : newData.entrySet()) { // Changed from newData.keySet()
                int targetHeight = (int) ((double) entry.getValue() / maxValue * maxHeight);
                targetHeights.put(entry.getKey(), targetHeight); // Fixed to use entry.getKey() and targetHeight
            }

            // Animate to new values
            int steps = 20;
            animationTimer = new Timer(ANIMATION_DURATION / steps, e -> {
                boolean animationComplete = true;
                
                for (String key : targetHeights.keySet()) {
                    int current = currentHeights.get(key);
                    int target = targetHeights.get(key);
                    
                    if (current != target) {
                        animationComplete = false;
                        int step = (target - current) / steps;
                        if (step == 0) step = target > current ? 1 : -1;
                        currentHeights.put(key, current + step);
                    }
                }
                
                repaint();
                
                if (animationComplete) {
                    ((Timer)e.getSource()).stop();
                    this.data = newData;
                }
            });
            
            animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (getWidth() - (BAR_WIDTH * 4 + BAR_SPACING * 3)) / 2;
            int baseY = getHeight() - 40;

            // Calculate maximum value for scaling
            int maxValue = 0;
            for (int value : data.values()) {
                maxValue = Math.max(maxValue, value);
            }

            // Set scale factor
            double scaleFactor = maxValue > 0 ? (baseY - 60) / (double) maxValue : 1;

            // Determine graph type based on panel name
            boolean isQuizGraph = "quiz_stats".equals(getName());

            // Draw bars based on graph type
            if (!isQuizGraph) {
                // Task statistics bars
                drawBar(g2, x, baseY, "Pending", PENDING_COLOR, scaleFactor);
                drawBar(g2, x + BAR_WIDTH + BAR_SPACING, baseY, "Completed", COMPLETED_COLOR, scaleFactor);
                drawBar(g2, x + (BAR_WIDTH + BAR_SPACING) * 2, baseY, "Total", TOTAL_COLOR, scaleFactor);
            } else {
                // Quiz statistics bars
                drawBar(g2, x, baseY, "Total Items", TOTAL_ITEMS_COLOR, scaleFactor);
                drawBar(g2, x + BAR_WIDTH + BAR_SPACING, baseY, "Avg First Score", AVG_SCORE_COLOR, scaleFactor);
                drawBar(g2, x + (BAR_WIDTH + BAR_SPACING) * 2, baseY, "Correct", CORRECT_COLOR, scaleFactor);
                drawBar(g2, x + (BAR_WIDTH + BAR_SPACING) * 3, baseY, "Incorrect", INCORRECT_COLOR, scaleFactor);
            }

            // Draw axes
            g2.setColor(TEXT_DARK);
            g2.drawLine(30, 20, 30, baseY);
            g2.drawLine(30, baseY, getWidth() - 30, baseY);
        }

        private void drawBar(Graphics2D g2, int x, int baseY, String label, Color color, double scaleFactor) {
            String key = label.toLowerCase().replace(" ", "_");
            int value = data.getOrDefault(key, 0);
            int height = (int) (value * scaleFactor);

            // Ensure minimum visible height if value is not 0
            if (value > 0 && height < 5) {
                height = 5;
            }

            // Draw bar
            g2.setColor(color);
            g2.fillRoundRect(x, baseY - height, BAR_WIDTH, height, 10, 10);

            // Draw border
            g2.setColor(color.darker());
            g2.drawRoundRect(x, baseY - height, BAR_WIDTH, height, 10, 10);

            // Draw label
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, x + (BAR_WIDTH - labelWidth) / 2, baseY + 20);

            // Draw value
            String valueStr = String.valueOf(value);
            int valueWidth = fm.stringWidth(valueStr);
            g2.drawString(valueStr, x + (BAR_WIDTH - valueWidth) / 2, baseY - height - 5);
        }
    }

    public static JPanel createTaskBarGraph() {
        JPanel container = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 300));
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        JLabel titleLabel = new JLabel("Task Statistics");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Bar Graph
        taskBarGraph = new CustomBarGraph();

        // Add components
        container.add(headerPanel, BorderLayout.NORTH);
        container.add(taskBarGraph, BorderLayout.CENTER);

        // Initial refresh instead of auto-refresh
        refreshTaskGraph();

        return container;
    }

    public static void refreshTaskGraph() {
        if (taskBarGraph != null) {
            try {
                Map<String, Integer> taskCounts = getTaskCounts();
                taskBarGraph.updateData(taskCounts);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static Map<String, Integer> getTaskCounts() {
        System.out.println("[Overview Panel] Fetching task counts at");
        Map<String, Integer> counts = new HashMap<>();
        try {
            String query = "SELECT " +
                         "COUNT(*) as total, " +
                         "SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending, " +
                         "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed " +
                         "FROM tasks WHERE user_id = ?";
            
            ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());
            
            if (rs.next()) {
                counts.put("pending", rs.getInt("pending"));
                counts.put("completed", rs.getInt("completed"));
                counts.put("total", rs.getInt("total"));
                System.out.println("[Overview Panel] Task counts fetched: " + counts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            counts.put("pending", 0);
            counts.put("completed", 0);
            counts.put("total", 0);
        }
        return counts;
    }

    public static JPanel createOverview(String username) {
        JPanel mainPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(20, 20, 20, 20);

        // Add statistics header
        gbc.gridy = 0;
        mainPanel.add(createStatisticsHeader(), gbc);

        // Add the task bar graph
        gbc.gridy = 1;
        mainPanel.add(createTaskBarGraph(), gbc);

        // Add the quiz statistics bar graph
        gbc.gridy = 2;
        mainPanel.add(createQuizStatsBarGraph(), gbc);

        // Initial refresh of all components
        refreshTaskGraph();

        return mainPanel;
    }

    private static JPanel createStatisticsHeader() {
        JPanel headerPanel = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 20, 0, 20)
        ));

        JLabel headerLabel = new JLabel("STATISTICS");
        headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerLabel.setForeground(TEXT_DARK);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    public static JPanel createQuizStatsBarGraph() {
        JPanel container = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 300));
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        JLabel titleLabel = new JLabel("Quiz Statistics");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Bar Graph
        quizStatsBarGraph = new CustomBarGraph();
        // Add a marker to identify this as a quiz graph
        quizStatsBarGraph.setName("quiz_stats");

        // Add components
        container.add(headerPanel, BorderLayout.NORTH);
        container.add(quizStatsBarGraph, BorderLayout.CENTER);

        return container;
    }

}
