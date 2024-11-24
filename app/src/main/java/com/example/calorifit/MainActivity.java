package com.example.calorifit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import database.FirebaseDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView greetingText = findViewById(R.id.greeting_text);

        // Get the user ID from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("CaloriFitPrefs", MODE_PRIVATE);
        String userId = preferences.getString("userId", null);

        FirebaseDatabaseHelper.retrieveString("users/" + userId + "/name", profileName -> {
            if (profileName != null) {
                greetingText.setText("Hello, " + profileName + "!");
            } else {
                greetingText.setText("Hello, User!");
            }
        });
    }
}
