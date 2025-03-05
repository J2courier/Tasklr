package tasklr.main.TaskPanel;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tasklr.authentication.UserSession;

public class TaskFetcher {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tasklrdb";
    private static final String DB_USER = "JFCompany";
    private static final String DB_PASSWORD = "";

    public static List<String[]> getUserTasks() {
        List<String[]> tasks = new ArrayList<>();
        int userId = UserSession.getUserId();
    
        System.out.println("🔍 Fetching tasks for user ID: " + userId); // Debugging
    
        if (userId == -1) {
            System.out.println("❌ No user session found!");
            return tasks; // Return empty list
        }
    
        String query = "SELECT title, status FROM tasks WHERE user_id = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                String title = rs.getString("title");
                String status = rs.getString("status");
                System.out.println("📌 Retrieved task: " + title + " - " + status); // Debugging
                tasks.add(new String[]{title, status});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
        return tasks;
    }

    public Map<String, Integer> getTaskCounts() {
        Map<String, Integer> counts = new HashMap<>();
        int userId = UserSession.getUserId(); // Changed from getCurrentUserId() to getUserId()

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get total tasks count
            String totalQuery = "SELECT COUNT(*) as total FROM tasks WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(totalQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    counts.put("total", rs.getInt("total"));
                }
            }

            // Get pending tasks count
            String pendingQuery = "SELECT COUNT(*) as pending FROM tasks WHERE user_id = ? AND status = 'pending'";
            try (PreparedStatement stmt = conn.prepareStatement(pendingQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    counts.put("pending", rs.getInt("pending"));
                }
            }

            // Get completed tasks count
            String completedQuery = "SELECT COUNT(*) as completed FROM tasks WHERE user_id = ? AND status = 'completed'";
            try (PreparedStatement stmt = conn.prepareStatement(completedQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    counts.put("completed", rs.getInt("completed"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return counts;
    }
}
