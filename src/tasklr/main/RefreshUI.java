package tasklr.main;

import javax.swing.*;
import tasklr.main.overveiw.TaskCounterPanel;
import tasklr.TaskPanel.TaskFetcher;
import java.util.Map;

public class RefreshUI extends SwingWorker<Void, Void> {
    private final TaskCounterPanel totalTasksPanel;
    private final TaskCounterPanel pendingTasksPanel;
    private final TaskCounterPanel completedTasksPanel;
    private final TaskFetcher taskFetcher;
    private volatile boolean running = true; // ✅ Ensures the loop stops properly

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
        while (running && !isCancelled()) { // ✅ Ensures the worker stops when canceled
            try {
                Map<String, Integer> taskCounts = taskFetcher.getTaskCounts();
                
                if (taskCounts != null) { // ✅ Prevent NullPointerException
                    SwingUtilities.invokeLater(() -> {
                        totalTasksPanel.updateCount(taskCounts.getOrDefault("total", 0));
                        pendingTasksPanel.updateCount(taskCounts.getOrDefault("pending", 0));
                        completedTasksPanel.updateCount(taskCounts.getOrDefault("completed", 0));
                    });
                }

                Thread.sleep(1000); // ✅ Refresh every 3 seconds
            } catch (InterruptedException e) {
                running = false; // ✅ Stop the loop if interrupted
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void done() {
        System.out.println("RefreshUI task stopped."); // ✅ Debugging message
    }
}
