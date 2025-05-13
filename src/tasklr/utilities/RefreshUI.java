package tasklr.utilities;

import javax.swing.*;

import tasklr.main.ui.components.TaskCounterPanel;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;

import java.util.Map;

public class RefreshUI extends SwingWorker<Void, Void> {
    private final TaskCounterPanel totalTasksPanel;
    private final TaskCounterPanel pendingTasksPanel;
    private final TaskCounterPanel completedTasksPanel;
    private final TaskFetcher taskFetcher;
    private volatile boolean running = true; 

    public RefreshUI(TaskCounterPanel totalTasksPanel, 
                     TaskCounterPanel pendingTasksPanel, 
                     TaskCounterPanel completedTasksPanel) {
        this.totalTasksPanel = totalTasksPanel;
        this.pendingTasksPanel = pendingTasksPanel;
        this.completedTasksPanel = completedTasksPanel;
        this.taskFetcher = new TaskFetcher();
    }

    @Override
    protected Void doInBackground() {
        while (running && !isCancelled()) { 
            try {
                Map<String, Integer> taskCounts = taskFetcher.getTaskCounts();
                
                if (taskCounts != null) { 
                    SwingUtilities.invokeLater(() -> {
                        totalTasksPanel.updateCount(taskCounts.getOrDefault("total", 0));
                        pendingTasksPanel.updateCount(taskCounts.getOrDefault("pending", 0));
                        completedTasksPanel.updateCount(taskCounts.getOrDefault("completed", 0));
                    });
                }

                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                running = false; 
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void done() {
        System.out.println("RefreshUI task stopped."); 
    }
}
