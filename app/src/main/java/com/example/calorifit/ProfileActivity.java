package com.example.calorifit;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import database.UserProfile;
public class ProfileActivity extends AppCompatActivity {

    private TextView nameValue, ageValue, weightValue, heightValue, weightGoalValue, caloricGoalValue;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firestore = FirebaseFirestore.getInstance();

        // Fetch UI components
        nameValue = findViewById(R.id.name_value);
        ageValue = findViewById(R.id.age_value);
        weightValue = findViewById(R.id.weight_value);
        heightValue = findViewById(R.id.height_value);
        weightGoalValue = findViewById(R.id.weight_goal_value);
        caloricGoalValue = findViewById(R.id.caloric_goal_value);

        // Load user data
        loadUserProfile();
    }

    /**
     * Fetch the user from the DB and initalize UI
     */
    private void loadUserProfile() {
        firestore.collection("User").document("Cf0ZElwKEA3iX1jkQZUQ")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfile user = documentSnapshot.toObject(UserProfile.class);
                        if (user != null) {
                            nameValue.setText(user.name);
                            ageValue.setText(String.valueOf(user.age));
                            weightValue.setText(String.valueOf(user.weight));
                            heightValue.setText(String.valueOf(user.height));
                            weightGoalValue.setText(String.valueOf(user.weightGoal));
                            caloricGoalValue.setText(String.valueOf(user.caloricGoal));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error loading user profile: " + e.getMessage());
                });
    }
}