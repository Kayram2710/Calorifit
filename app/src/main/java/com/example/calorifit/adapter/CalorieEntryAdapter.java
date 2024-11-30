package com.example.calorifit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorifit.R;

import java.util.List;

import database.CalorieEntry;

/**
 * Used for RecyclerView. Will be used to display calories entries for the search view.
 */
public class CalorieEntryAdapter extends RecyclerView.Adapter<CalorieEntryAdapter.EntryViewHolder> {
    private List<CalorieEntry> entryList;
    private OnEditClickListener editClickListener;
    private OnAddClickListener addClickListener;

    public interface OnEditClickListener {
        void onEditClick(CalorieEntry entry);
    }

    public interface OnAddClickListener {
        void onAddClick(CalorieEntry entry);
    }

    public CalorieEntryAdapter(List<CalorieEntry> entryList, OnEditClickListener listener, OnAddClickListener listener2) {
        this.entryList = entryList;
        this.editClickListener = listener;
        this.addClickListener = listener2;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calorie_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        CalorieEntry entry = entryList.get(position);
        holder.name.setText(entry.name);
        holder.calories.setText(entry.amount + " cal");
        holder.servingSize.setText(entry.type);
        holder.editButton.setOnClickListener(v -> editClickListener.onEditClick(entry));
        holder.addButton.setOnClickListener(v -> addClickListener.onAddClick(entry));
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView name, calories, servingSize;
        ImageButton editButton, addButton;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.foodName);
            calories = itemView.findViewById(R.id.foodCalories);
            servingSize = itemView.findViewById(R.id.foodServingSize);
            editButton = itemView.findViewById(R.id.removeButton);
            addButton = itemView.findViewById(R.id.addBtn);
        }
    }
}