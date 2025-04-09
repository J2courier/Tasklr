package tasklr.utilities;

import java.sql.*;
import java.util.*;
import tasklr.authentication.UserSession;

public class SearchManager {
    // Search types
    public static final String SEARCH_TASKS = "tasks";
    public static final String SEARCH_FLASHCARDS = "flashcards";
    
    public static List<Map<String, Object>> searchTasks(String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND " +
                    "(title LIKE ? OR description LIKE ? OR status LIKE ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setInt(1, UserSession.getUserId());
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> task = new HashMap<>();
                task.put("id", rs.getInt("id"));
                task.put("title", rs.getString("title"));
                task.put("status", rs.getString("status"));
                task.put("due_date", rs.getDate("due_date"));
                results.add(task);
            }
        } catch (SQLException e) {
            Toast.error("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    public static List<Map<String, Object>> searchFlashcards(String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT f.*, fs.subject FROM flashcards f " +
                    "JOIN flashcard_sets fs ON f.set_id = fs.set_id " +
                    "WHERE fs.user_id = ? AND " +
                    "(f.term LIKE ? OR f.definition LIKE ? OR fs.subject LIKE ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setInt(1, UserSession.getUserId());
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> flashcard = new HashMap<>();
                flashcard.put("id", rs.getInt("id"));
                flashcard.put("term", rs.getString("term"));
                flashcard.put("definition", rs.getString("definition"));
                flashcard.put("subject", rs.getString("subject"));
                results.add(flashcard);
            }
        } catch (SQLException e) {
            Toast.error("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    /**
     * Search for flashcards within a specific set
     * @param query The search term
     * @param setId The ID of the flashcard set being viewed
     * @return List of matching flashcards
     */
    public static List<Map<String, Object>> searchFlashcardsInSet(String query, int setId) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT f.*, fs.subject " +
                    "FROM flashcards f " +
                    "JOIN flashcard_sets fs ON f.set_id = fs.set_id " +
                    "WHERE f.set_id = ? AND fs.user_id = ? " +
                    "AND (f.term LIKE ? OR f.definition LIKE ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setInt(1, setId);
            stmt.setInt(2, UserSession.getUserId());
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> flashcard = new HashMap<>();
                flashcard.put("term", rs.getString("term"));
                flashcard.put("definition", rs.getString("definition"));
                flashcard.put("subject", rs.getString("subject"));
                results.add(flashcard);
            }
        } catch (SQLException e) {
            Toast.error("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    // Generic search method that can be used for different types
    public static List<Map<String, Object>> search(String query, String type) {
        return switch (type) {
            case SEARCH_TASKS -> searchTasks(query);
            case SEARCH_FLASHCARDS -> searchFlashcards(query);
            default -> new ArrayList<>();
        };
    }
}
