package database;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserProfile {
    public String id; // Unique user ID
    public String name;
    public int age;
    public int weight;
    public int height;
    public int weightGoal;
    public int caloricGoal;
    public HashMap<String, DocumentReference> days; // Log of days

    public UserProfile() {}

    public UserProfile(String id, String name, int age, int weight, int height, int weightGoal, int caloricGoal) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.weightGoal = weightGoal;
        this.days = new HashMap<>();
        this.caloricGoal = caloricGoal;
    }


}
