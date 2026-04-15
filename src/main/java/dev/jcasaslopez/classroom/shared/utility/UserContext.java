package dev.jcasaslopez.classroom.shared.utility;

// Holds the user's email extracted from the JWT during filter validation.
// Avoids calling User micro-service just to retrieve the email, keeping micro-services decoupled.
public class UserContext {

    private static final ThreadLocal<String> emailHolder = new ThreadLocal<>();

    public static void setEmail(String email) {
        emailHolder.set(email);
    }

    public static String getEmail() {
        return emailHolder.get();
    }

    public static void clear() {
        emailHolder.remove(); 
    }
}
