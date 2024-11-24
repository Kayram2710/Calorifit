package com.example.calorifit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import database.*; // Import the helper from the database package

public class ProfileFormActivity extends AppCompatActivity {

    private EditText inputName, inputAge, inputWeight, inputHeight, inputWeightGoal;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_form);

        inputName = findViewById(R.id.input_name);
        inputAge = findViewById(R.id.input_age);
        inputWeight = findViewById(R.id.input_weight);
        inputHeight = findViewById(R.id.input_height);
        inputWeightGoal = findViewById(R.id.input_weight_goal);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> saveUserProfile());
    }

    private void saveUserProfile() {
        String name = inputName.getText().toString();
        int age = Integer.parseInt(inputAge.getText().toString());
        int weight = Integer.parseInt(inputWeight.getText().toString());
        int height = Integer.parseInt(inputHeight.getText().toString());
        int weightGoal = Integer.parseInt(inputWeightGoal.getText().toString());

        // Generate a unique user ID
        String userId = UUID.randomUUID().toString();

        // Create UserProfile object
        UserProfile userProfile = new UserProfile(userId, name, age, weight, height, weightGoal);

        // Save profile to Firebase
        FirebaseDatabaseHelper.saveUserProfile(userProfile);

        // Save user ID in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("CaloriFitPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", userId);
        editor.apply();

        // Move to the next activity
        Intent intent = new Intent(ProfileFormActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
