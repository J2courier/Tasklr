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

    private static TaskCounterPanel pendingTasksPanel;
    private static TaskCounterPanel completedTasksPanel;
    private static TaskCounterPanel totalTasksPanel;
    
    // Add new panel variables for flashcard statistics
    private static TaskCounterPanel totalFlashcardSetsPanel;
    private static TaskCounterPanel pendingQuizProgressPanel;
    private static TaskCounterPanel completedQuizProgressPanel;

    private static CustomBarGraph barGraph;

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

            int x = (getWidth() - (BAR_WIDTH * 3 + BAR_SPACING * 2)) / 2;
            int baseY = getHeight() - 40;

            // Draw bars
            drawBar(g2, x, baseY, "Pending", PENDING_COLOR);
            drawBar(g2, x + BAR_WIDTH + BAR_SPACING, baseY, "Completed", COMPLETED_COLOR);
            drawBar(g2, x + (BAR_WIDTH + BAR_SPACING) * 2, baseY, "Total", TOTAL_COLOR);

            // Draw Y-axis
            g2.setColor(TEXT_DARK);
            g2.drawLine(30, 20, 30, baseY);
            
            // Draw X-axis
            g2.drawLine(30, baseY, getWidth() - 30, baseY);
        }

        private void drawBar(Graphics2D g2, int x, int baseY, String label, Color color) {
            int height = currentHeights.getOrDefault(label.toLowerCase(), 0);
            int value = data.getOrDefault(label.toLowerCase(), 0);

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
        barGraph = new CustomBarGraph();

        // Add components
        container.add(headerPanel, BorderLayout.NORTH);
        container.add(barGraph, BorderLayout.CENTER);

        // Start auto-refresh
        startGraphRefresh();

        return container;
    }

    private static void startGraphRefresh() {
        Timer timer = new Timer(2000, e -> {
            if (barGraph != null) {
                try {
                    Map<String, Integer> taskCounts = getTaskCounts();
                    barGraph.updateData(taskCounts);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        timer.start();
    }

    private static Map<String, Integer> getTaskCounts() {
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

        // Add the bar graph
        gbc.gridy = 1;
        mainPanel.add(createTaskBarGraph(), gbc);

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
}
