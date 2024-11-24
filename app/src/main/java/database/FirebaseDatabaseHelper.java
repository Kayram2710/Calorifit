package database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {

    private static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private static final String node = "users";

    // User Profile Methods
    // Add or update a user profile
    public static void saveUserProfile(UserProfile userProfile) {
        db.child(node).child(userProfile.id).setValue(userProfile);
    }

    // Delete a user profile
    public static void deleteUserProfile(String userId) {
        db.child(node).child(userId).removeValue();
    }

    // Day Methods
    // Add or update a day for a user
    public static void saveDay(String userId, Day day) {
        db.child(node).child(userId).child("dayLog").child(day.date).setValue(day);
    }

    // Delete a day for a user
    public static void deleteDay(String userId, String dayId) {
        db.child(node).child(userId).child("dayLog").child(dayId).removeValue();
    }

    // Calorie Entry Methods
    // Add or update a calorie entry
    public static void saveCalorieEntry(String userId, String dayId, CalorieEntry entry) {
        db.child(node).child(userId).child("dayLog").child(dayId).child("calorieLog").child(entry.id)
                .setValue(entry);
    }

    // Delete a calorie entry
    public static void deleteCalorieEntry(String userId, String dayId, String entryId) {
        db.child(node).child(userId).child("dayLog").child(dayId).child("calorieLog").child(entryId)
                .removeValue();
    }

    // Retrieve a specific attribute as a String
    public static void retrieveString(String path, SimpleCallback<String> callback) {
        db.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Pass the retrieved value back to the callback
                    callback.onComplete(snapshot.getValue(String.class));
                } else {
                    // No data found
                    callback.onComplete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Pass null to indicate an error
                callback.onComplete(null);
                error.toException().printStackTrace();
            }
        });
    }

    // Callback interface
    public interface SimpleCallback<T> {
        void onComplete(T data);
    }
}

