package Models;

import java.util.ArrayList;
import java.util.List;

public class UserProfileList {

    private List<UserProfile> profiles;

    public UserProfileList() {
        this.profiles = new ArrayList<>();
    }

    public void displayProfiles() {
        for (UserProfile profile : profiles) {
            System.out.println("ID: " + profile.getUserId() + ", Name: " + profile.getName());
        }
    }

    public UserProfile selectProfile(int userId) {
        for (UserProfile profile : profiles) {
            if (profile.getUserId() == userId) {
                return profile;
            }
        }
        return null; // not found
    }

    public void addProfile(UserProfile profile) {
        profiles.add(profile);
    }

    public void deleteProfile(int userId) {
        profiles.removeIf(profile -> profile.getUserId() == userId);
    }

    // Optional: Getter if you want direct access to the list
    public List<UserProfile> getProfiles() {
        return profiles;
    }
}