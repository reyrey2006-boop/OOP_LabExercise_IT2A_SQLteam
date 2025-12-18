package utils;

public class UserSession {
    private static UserSession instance;
    private String fullName;
    private String email;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Call this after successful login
    public void setUser(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    
    public void logout() {
        fullName = null;
        email = null;
    }
}