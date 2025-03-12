package tasklr.main.ui.panels.overveiw;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import tasklr.main.ui.components.TaskCounterPanel;
import tasklr.utilities.RefreshUI;
import tasklr.utilities.createPanel;

public class overview {

    public static JPanel createOverview(String username) {            
        JPanel panel = createPanel.panel(null, new GridBagLayout(), new Dimension(400, 0));

        JPanel profile = createPanel.panel(new Color(0xE0E3E2), new BorderLayout(), new Dimension(400, 125));
        profile.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    
        JLabel userLabel = new JLabel("WELCOME, " + username + "!");
        userLabel.setForeground(Color.BLACK);
        userLabel.setFont(new Font("Arial", Font.BOLD, 26));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profile.add(userLabel, BorderLayout.WEST);

        TaskOverview task_overview_lbl = new TaskOverview();

        // ✅ Task Counter Panels
        TaskCounterPanel totalTasksPanel = new TaskCounterPanel(0, "Total Tasks");
        TaskCounterPanel pendingTasksPanel = new TaskCounterPanel(0, "Pending");
        TaskCounterPanel completedTasksPanel = new TaskCounterPanel(0, "Completed");

        // ✅ Start the refresh worker (moved down to avoid multiple calls)
        RefreshUI refreshUI = new RefreshUI(totalTasksPanel, pendingTasksPanel, completedTasksPanel);
        refreshUI.execute();

        JPanel spacer = createPanel.panel(null, null, new Dimension(100, 500));

        // ✅ Add Components
        addComponent(panel, profile, 0, 0, 3, 1, new Insets(0, 0, 0, 0));
        addComponent(panel, task_overview_lbl.createTaskOverviewHeader(), 0, 2, 3, 1, new Insets(0, 0, 10, 0));
        addComponent(panel, pendingTasksPanel.createPanel(), 0, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, completedTasksPanel.createPanel(), 1, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, totalTasksPanel.createPanel(), 2, 4, 1, 1, new Insets(5, 0, 10, 5));
        addComponent(panel, spacer, 0, 8, 3, 1, new Insets(5, 5, 5, 5));

        return panel;
    }

    private static void addComponent(JPanel panel, JComponent comp, int x, int y, int width, int height, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = insets;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        panel.add(comp, gbc);
    }
}
