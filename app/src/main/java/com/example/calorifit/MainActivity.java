package com.example.calorifit;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import database.CalorieEntry;
import database.Day;
import database.UserProfile;

/**
 * Responsible for the main menu. Can be used to add new meals and access the rest of the features
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private Day currentDay;
    //Reset objects
    @Override
    protected void onResume() {
        super.onResume();
        fetchObjects("Cf0ZElwKEA3iX1jkQZUQ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        //Fetch the objects to properly initialize UI
        fetchObjects("Cf0ZElwKEA3iX1jkQZUQ");
        //Set Onclick events for the buttons
        ImageButton calender = findViewById(R.id.button_calendar);
        calender.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        ImageButton add = findViewById(R.id.searchFoodBtn);
        add.setOnClickListener(v -> {
            validateNewFood();
        });

        ImageButton searchBtn = findViewById(R.id.button_search_food);
        searchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        ImageButton profileBtn = findViewById(R.id.button_profile);
        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

    }

    /**
     * Fetches the day associated to the user in order to update the UI with the current calories vs goal calories
     * @param userId
     */
    public void fetchObjects(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User").document(userId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                        if (userProfile != null) {
                            //Handles the case where the day entry does not exist (no entries for the given day)
                            if(!userProfile.days.containsKey(getCurrentDate())){
                                Day day = new Day(getCurrentDate(), userProfile.caloricGoal, 0, new ArrayList<>());
                                saveDay(day,userId);
                                initializePage(day);
                            }
                            //Handles the case where the day already exists
                            else {
                                userProfile.days.get(getCurrentDate()).get()
                                        .addOnSuccessListener(daySnapshot -> {
                                            if (daySnapshot.exists()) {
                                                Day day = daySnapshot.toObject(Day.class);
                                                initializePage(day);
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    /**
     * Returns the current date in the format used in the DB for entries
     * @return
     */
    public static String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        return String.valueOf(month) + "-" + String.valueOf(day) + "-"+ String.valueOf(year);
    }

    /**
     * Saves the day to the DB and calls updateUserProfileWithDay to update the user with the new day reference (only used when the day is created for the first time)
     * @param day
     * @param userId
     */
    private void saveDay(Day day, String userId){
        firestore.collection("Day")
                .document(day.date)
                .set(day)
                .addOnSuccessListener(aVoid -> {
                    //Update User with day reference
                    updateUserProfileWithDay(userId, day.date);
                });

    }

    /**
     * Updates the user by adding a reference to the current day
     * @param userId
     * @param dayDate
     */
    public void updateUserProfileWithDay(String userId, String dayDate) {
        // Get the reference to the saved day document
        DocumentReference dayRef = firestore.collection("Day")
                .document(dayDate);

        // Update the user's profile with the reference to this day
        firestore.collection("User").document(userId)
                .update("days." + dayDate, dayRef)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User profile updated with new day.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating user profile: " + e.getMessage());
                });
    }

    /**
     * Updates the day by adding a calorie entry to it in the DB.
     * @param ce CalorieEntry
     * @param firestore FirebaseFirestore
     */
    public static void updateDayWithCalorieEntry(CalorieEntry ce, FirebaseFirestore firestore) {
        // Get the reference to the CalorieEntry
        DocumentReference ceRef = firestore.collection("CalorieEntry").document(ce.name);
        //Updates the entry
        firestore.collection("Day").document(getCurrentDate())
                .update("caloriesEntries", FieldValue.arrayUnion(ceRef));
    }

    /**
     * Initializes the UI with the proper data from Day
     * @param day
     */
    private void initializePage(Day day){
        currentDay = day;
        TextView calories = findViewById(R.id.calories);
        calories.setText(day.currentCalories + "/" +day.calorieGoal+ "cal");
        TextView caloriesLeft = findViewById(R.id.calories_left);
        caloriesLeft.setText(day.currentCalories - day.calorieGoal + " left over");
    }

    /**
     * Updates day in the DB
     */
    private void updateDay(){
        DocumentReference dayRef = firestore.collection("Day").document(getCurrentDate());
        dayRef.set(currentDay)
                .addOnSuccessListener(aVoid -> {
                    initializePage(currentDay);
                });
    }

    /**
     * Validates that the input for the new food is correct. Adds it to the DB and updates the Day + UI
     */
    private void validateNewFood(){
        //Fetching UI Components
        EditText calories = findViewById(R.id.foodSearch);
        EditText foodName = findViewById(R.id.foodName);
        EditText foodType = findViewById(R.id.foodType);
        //Make sure all fields are not empty
        if(calories.getText().toString().trim().isEmpty() || foodName.getText().toString().trim().isEmpty() || foodType.getText().toString().trim().isEmpty()){
            Toast.makeText(MainActivity.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
        }else{
            try{
                //Save CalorieEntry
                CalorieEntry ce = new CalorieEntry(foodName.getText().toString(), Integer.parseInt(calories.getText().toString()), foodType.getText().toString());
                firestore.collection("CalorieEntry")
                        .document(ce.name) // Use the date as the document ID
                        .set(ce)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Entry added successfully!", Toast.LENGTH_SHORT).show();
                            calories.setText("");
                            foodName.setText("");
                            foodType.setText("");
                            //Adding new calories to day and updating it.
                            currentDay.currentCalories += ce.amount;
                            updateDay();
                            //Updating Day with reference to new CalorieEntry
                            updateDayWithCalorieEntry(ce,firestore);
                        });
            } catch (NumberFormatException e){
                Toast.makeText(this, "Please enter a valid integer", Toast.LENGTH_SHORT).show();
            }
        }

    }
}