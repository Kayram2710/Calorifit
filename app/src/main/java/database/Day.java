package database;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day {
    public String date; // Unique ID (date)
    public List<DocumentReference> caloriesEntries; // Log of calorie entries
    public int calorieGoal; // Daily calorie goal
    public int currentCalories; // Total calories consumed so far

    public Day() {}

    public Day(String date, int calorieGoal, int currentCalories, List<DocumentReference> caloriesEntries ) {
        this.date = date;
        this.calorieGoal = calorieGoal;
        this.currentCalories = currentCalories;
        this.caloriesEntries = new ArrayList<>();
    }
}
