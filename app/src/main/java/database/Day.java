package database;

import java.util.HashMap;

public class Day {
    public String date; // Unique ID (date)
    public HashMap<String, CalorieEntry> calorieLog; // Log of calorie entries
    public int calorieGoal; // Daily calorie goal
    public int currentCalories; // Total calories consumed so far

    public Day() {}

    public Day(String date, int calorieGoal, int currentCalories) {
        this.date = date;
        this.calorieGoal = calorieGoal;
        this.currentCalories = currentCalories;
        this.calorieLog = new HashMap<>();
    }
}
