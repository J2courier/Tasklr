package tasklr.utilities;

import javax.swing.*;
import java.util.concurrent.*;
import java.util.Map;

public class UIRefreshManager {
    private static UIRefreshManager instance;
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> refreshTasks;
    private static final int DEFAULT_REFRESH_RATE = 5000;

    public static final String TASK_COUNTER = "task_counter";
    public static final String TASK_GRAPH = "task_graph";
    public static final String QUIZ_STATS = "quiz_stats";
    public static final String TASK_LIST = "task_list";
    public static final String QUIZ_CONTAINER = "quiz_container";

    private UIRefreshManager() {
        scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        refreshTasks = new ConcurrentHashMap<>();
    }

    public static synchronized UIRefreshManager getInstance() {
        if (instance == null) {
            instance = new UIRefreshManager();
        }
        return instance;
    }

    public void startRefresh(String taskId, Runnable refreshTask) {
        startRefresh(taskId, refreshTask, DEFAULT_REFRESH_RATE);
    }

    public void startRefresh(String taskId, Runnable refreshTask, int refreshRate) {
        stopRefresh(taskId); 
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> SwingUtilities.invokeLater(() -> {
                try {
                    refreshTask.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.error("Refresh failed: " + e.getMessage());
                }
            }),
            0, refreshRate, TimeUnit.MILLISECONDS
        );
        
        refreshTasks.put(taskId, future);
    }

    public void stopRefresh(String taskId) {
        ScheduledFuture<?> future = refreshTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }

    public void stopAllRefresh() {
        refreshTasks.forEach((taskId, future) -> future.cancel(false));
        refreshTasks.clear();
    }

    public boolean isRefreshing(String taskId) {
        ScheduledFuture<?> future = refreshTasks.get(taskId);
        return future != null && !future.isCancelled();
    }
}



