package tasklr.TaskPanel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tasklr.authentication.UserSession;

public class TaskFetcher {
    public static List<String[]> getUserTasks() {
        List<String[]> tasks = new ArrayList<>();
        int userId = UserSession.getUserId();
    
        System.out.println("🔍 Fetching tasks for user ID: " + userId); // Debugging
    
        if (userId == -1) {
            System.out.println("❌ No user session found!");
            return tasks; // Return empty list
        }
    
        String query = "SELECT title, status FROM tasks WHERE user_id = ?";
    
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tasklrdb", "JFCompany", "");
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
    
}
