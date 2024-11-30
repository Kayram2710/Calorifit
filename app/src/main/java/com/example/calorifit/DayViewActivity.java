package com.example.calorifit;

import static com.example.calorifit.MainActivity.getCurrentDate;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorifit.adapter.CalorieEntryAdapter;
import com.example.calorifit.adapter.DayViewAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import database.CalorieEntry;
import database.Day;

/**
 * Responsible for the day view which displays the meals eaten in the day.
 */
public class DayViewActivity extends AppCompatActivity {

    private RecyclerView recyclerBreakfast, recyclerLunch, recyclerDinner, recyclerSnacks;
    private DayViewAdapter breakfastAdapter, lunchAdapter, dinnerAdapter, snacksAdapter;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayview);

        //Fetching the objects by id
        recyclerBreakfast = findViewById(R.id.recyclerBreakfast);
        recyclerLunch = findViewById(R.id.recyclerLunch);
        recyclerDinner = findViewById(R.id.recyclerDinner);
        recyclerSnacks = findViewById(R.id.recyclerSnacks);
        //Setting the layout manager for the adapter
        recyclerBreakfast.setLayoutManager(new LinearLayoutManager(this));
        recyclerLunch.setLayoutManager(new LinearLayoutManager(this));
        recyclerDinner.setLayoutManager(new LinearLayoutManager(this));
        recyclerSnacks.setLayoutManager(new LinearLayoutManager(this));

        loadDayViewEntries();
    }

    /**
     * Loads all the meals for the day. Gives every meal to its appropriate adapter (Lunch,Breakfast, Dinner or Snacks)
     */
    private void loadDayViewEntries() {
        // Get the current day from Firestore
        firestore.collection("Day").document(getCurrentDate())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lists for entries Breakfast, Lunch, Dinner, Snacks
                        List<CalorieEntry> breakfast = new ArrayList<>();
                        List<CalorieEntry> lunch = new ArrayList<>();
                        List<CalorieEntry> dinner = new ArrayList<>();
                        List<CalorieEntry> snacks = new ArrayList<>();

                        // Get the list of CalorieEntry references
                        List<DocumentReference> entries = (List<DocumentReference>) documentSnapshot.get("caloriesEntries");

                        // Fetch each CalorieEntry document
                        for (DocumentReference entryRef : entries) {
                            entryRef.get().addOnSuccessListener(entryDoc -> {
                                if (entryDoc.exists()) {
                                    CalorieEntry entry = entryDoc.toObject(CalorieEntry.class);
                                    // Switch statment for the entries
                                    switch (entry.type.toLowerCase()) {
                                        case "breakfast":
                                            breakfast.add(entry);
                                            break;
                                        case "lunch":
                                            lunch.add(entry);
                                            break;
                                        case "dinner":
                                            dinner.add(entry);
                                            break;
                                        case "snack":
                                            snacks.add(entry);
                                            break;
                                    }
                                    // Update the RecyclerViews
                                    updateRecyclerViews(breakfast, lunch, dinner, snacks);
                                }
                            });
                        }
                    }
                });
    }

    private void updateRecyclerViews(List<CalorieEntry> breakfast, List<CalorieEntry> lunch,
                                     List<CalorieEntry> dinner, List<CalorieEntry> snacks) {
        breakfastAdapter = new DayViewAdapter(breakfast, entry -> removeCalorieEntry(entry));
        lunchAdapter = new DayViewAdapter(lunch, entry -> removeCalorieEntry(entry));
        dinnerAdapter = new DayViewAdapter(dinner, entry -> removeCalorieEntry(entry));
        snacksAdapter = new DayViewAdapter(snacks, entry -> removeCalorieEntry(entry));

        recyclerBreakfast.setAdapter(breakfastAdapter);
        recyclerLunch.setAdapter(lunchAdapter);
        recyclerDinner.setAdapter(dinnerAdapter);
        recyclerSnacks.setAdapter(snacksAdapter);
    }

    /**
     * Removes the Entry from the DB and the UI
     * @param entry
     */
    private void removeCalorieEntry(CalorieEntry entry) {
        DocumentReference ceRef = firestore.collection("CalorieEntry").document(entry.name);
        firestore.collection("Day").document(getCurrentDate())
                .update("caloriesEntries", FieldValue.arrayRemove(ceRef))
                .addOnSuccessListener(aVoid -> {
                    // Fetch the Day document to update currentCalories
                    firestore.collection("Day").document(getCurrentDate())
                            .get()
                            .addOnSuccessListener(daySnapshot -> {
                                if (daySnapshot.exists()) {
                                    Day day = daySnapshot.toObject(Day.class);
                                    if (day != null) {
                                        // Subtracting the removed entry's calories from currentCalories
                                        day.currentCalories -= entry.amount;

                                        // Updating the Day document with the new currentCalories value
                                        firestore.collection("Day").document(getCurrentDate())
                                                .update("currentCalories", day.currentCalories)
                                                .addOnSuccessListener(updateVoid -> {
                                                    Toast.makeText(this, "Calories updated successfully", Toast.LENGTH_SHORT).show();

                                                    // Update UI after successfully removing the entry from DB
                                                    switch (entry.type.toLowerCase()) {
                                                        case "breakfast":
                                                            breakfastAdapter.removeItem(entry);
                                                            break;
                                                        case "lunch":
                                                            lunchAdapter.removeItem(entry);
                                                            break;
                                                        case "dinner":
                                                            dinnerAdapter.removeItem(entry);
                                                            break;
                                                        case "snacks":
                                                            snacksAdapter.removeItem(entry);
                                                            break;
                                                    }
                                                });
                                    }
                                }
                            });
                });
    }

}