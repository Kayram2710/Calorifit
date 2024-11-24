package database;

import java.util.HashMap;

public class UserProfile {
    public String id; // Unique user ID
    public String name;
    public int age;
    public int weight;
    public int height;
    public int weightGoal;
    public HashMap<String, Day> dayLog; // Log of days

    public UserProfile() {}

    public UserProfile(String id, String name, int age, int weight, int height, int weightGoal) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.weightGoal = weightGoal;
        this.dayLog = new HashMap<>();
    }
}
