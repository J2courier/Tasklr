package tasklr.authentication;

public class UserSession {
    private static UserSession instance;
    private int userId;
    private String username;
    private String sessionToken;

    private UserSession(int userId, String username, String sessionToken) {
        this.userId = userId;
        this.username = username;
        this.sessionToken = sessionToken;
    }

    public static void createSession(int userId, String username, String sessionToken) {
        instance = new UserSession(userId, username, sessionToken);
    }

    public static UserSession getSession() {
        return instance;
    }

    public static void clearSession() {
        instance = null;
    }

    public static int getUserId() {
        return (instance != null) ? instance.userId : -1;
    }
    
    public String getUsername() {
        return username;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}
