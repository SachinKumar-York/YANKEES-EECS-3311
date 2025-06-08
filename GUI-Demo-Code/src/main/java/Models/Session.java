package Models;


public class Session {
    private static int currentUserId = -1;

    public static void login(int userId) {
        currentUserId = userId;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static boolean isLoggedIn() {
        return currentUserId > 0;
    }
}

