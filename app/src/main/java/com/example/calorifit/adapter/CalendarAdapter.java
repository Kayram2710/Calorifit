package com.example.calorifit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorifit.DayViewActivity;
import com.example.calorifit.R;

import java.util.List;

/**
 * Used for RecyclerView. Will be used to display days in the calendar.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private Context context;
    private List<String> days;
    private int todayDay;

    public CalendarAdapter(Context context, List<String> days, int todayDay) {
        this.context = context;
        this.days = days;
        this.todayDay = todayDay;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days.get(position);
        holder.textDay.setText(day);

        // Handle empty days (placeholders)
        if (day.isEmpty()) {
            holder.textDay.setVisibility(View.INVISIBLE); // Hide placeholder days
        } else {
            holder.textDay.setVisibility(View.VISIBLE);
            if (Integer.parseInt(day) == todayDay) {
                holder.statusDot.setVisibility(View.VISIBLE); // Show today's dot
                holder.itemView.setOnClickListener(v -> {
                    // Pass the date to the DayViewActivity
                    Intent intent = new Intent(context, DayViewActivity.class);
                    intent.putExtra("selectedDate", day);  // Pass the selected day to DayViewActivity
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDay;
        View statusDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.text_day);
            statusDot = itemView.findViewById(R.id.status_dot);
        }
    }
}