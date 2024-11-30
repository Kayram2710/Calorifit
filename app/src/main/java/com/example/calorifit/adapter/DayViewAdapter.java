package com.example.calorifit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorifit.R;

import java.util.List;

import database.CalorieEntry;
/**
 * Used for RecyclerView. Will be used to display eaten meals in the day view.
 */
public class DayViewAdapter extends RecyclerView.Adapter<DayViewAdapter.ViewHolder> {

    private List<CalorieEntry> calorieEntries;
    private OnRemoveClickListener onRemoveClickListener;

    public DayViewAdapter(List<CalorieEntry> calorieEntries, OnRemoveClickListener listener) {
        this.calorieEntries = calorieEntries;
        this.onRemoveClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dayview_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalorieEntry entry = calorieEntries.get(position);
        holder.foodName.setText(entry.name);
        holder.foodCalories.setText(String.valueOf(entry.amount) + " cal");
        holder.removeButton.setOnClickListener(v -> onRemoveClickListener.onRemoveClick(entry));
    }

    @Override
    public int getItemCount() {
        return calorieEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView foodCalories;
        ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodCalories = itemView.findViewById(R.id.foodCalories);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    /**
     * Removes the entry from the UI
     * @param entry
     */
    public void removeItem(CalorieEntry entry) {
        int position = calorieEntries.indexOf(entry);
        if (position != -1) {
            calorieEntries.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(CalorieEntry entry);
    }
}
