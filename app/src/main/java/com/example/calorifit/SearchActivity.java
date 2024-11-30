package com.example.calorifit;

import static com.example.calorifit.MainActivity.getCurrentDate;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorifit.adapter.CalorieEntryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.CalorieEntry;
import database.Day;
import database.UserProfile;

public class SearchActivity extends AppCompatActivity {
    private EditText searchQuery;
    private RecyclerView resultsRecyclerView;
    private CalorieEntryAdapter adapter;
    private List<CalorieEntry> entryList;
    FirebaseFirestore firestore;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        firestore = FirebaseFirestore.getInstance();
        userId = "Cf0ZElwKEA3iX1jkQZUQ";
        searchQuery = findViewById(R.id.foodSearch);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        ImageButton searchBtn = findViewById(R.id.searchFoodBtn);

        entryList = new ArrayList<CalorieEntry>();
        // Handle add click
        adapter = new CalorieEntryAdapter(entryList, entry -> {
            // Handle edit click
            Toast.makeText(this, "Edit: " + entry.name, Toast.LENGTH_SHORT).show();
        }, this::addEntry);

        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> performSearch());
    }

    /**
     * Search for entries in the DB that match the query text
     */
    private void performSearch() {
        String queryText = searchQuery.getText().toString().trim();
        //check if query is empty
        if (queryText.isEmpty()) {
            Toast.makeText(this, "Enter a search query", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        //searching for entry in DB
        firestore.collection("CalorieEntry")
                .whereGreaterThanOrEqualTo("name", queryText)
                .whereLessThanOrEqualTo("name", queryText + "\uf8ff")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    entryList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        CalorieEntry entry = doc.toObject(CalorieEntry.class);
                        entryList.add(entry);
                    }
                    //Notify adapter to update UI
                    adapter.notifyDataSetChanged();
                });
    }

    /**
     * Adds the given CalorieEntry to the DB
     * @param ce
     */
    private void addEntry(CalorieEntry ce){
        firestore.collection("CalorieEntry")
                .document(ce.name)
                .set(ce)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Entry added successfully!", Toast.LENGTH_SHORT).show();
                    updateDay(userId,ce);
                });


    }

    /**
     * Adds the given CalorieEntry to the current day in the DB
     * @param userId
     * @param ce
     */
    private void updateDay(String userId, CalorieEntry ce){
        DocumentReference userRef = firestore.collection("User").document(userId);
        //Get the user ref
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                        Map<String, Object> userData = documentSnapshot.getData();
                        Day day;
                        if (userProfile != null) {
                            //Case where the day does not exist
                            if(!userProfile.days.containsKey(getCurrentDate())){
                                day = new Day(getCurrentDate(), userProfile.caloricGoal, 0, new ArrayList<>());
                                //Update calories for day
                                day.currentCalories += ce.amount;
                                //Save
                                firestore.collection("Day")
                                        .document(day.date)
                                        .set(day).addOnSuccessListener(e -> {
                                            MainActivity.updateDayWithCalorieEntry(ce,firestore);
                                        });
                            }
                            //Case where the day does exist
                            else {
                                userProfile.days.get(getCurrentDate()).get()
                                        .addOnSuccessListener(daySnapshot -> {
                                            if (daySnapshot.exists()) {
                                                Day existingDay = daySnapshot.toObject(Day.class);
                                                //Updates calories for the day
                                                existingDay.currentCalories += ce.amount;
                                                //Save
                                                firestore.collection("Day")
                                                        .document(existingDay.date)
                                                        .set(existingDay).addOnSuccessListener(e -> {
                                                            MainActivity.updateDayWithCalorieEntry(ce,firestore);
                                                        });
                                            }
                                        });
                            }
                        }
                    }
                });
    }

}