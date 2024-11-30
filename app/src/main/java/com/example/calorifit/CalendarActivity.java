package com.example.calorifit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorifit.adapter.CalendarAdapter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Calendar activity. Will display days.
 */
public class CalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private TextView textMonthYear;
    private Button buttonPreviousMonth, buttonNextMonth;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize Views
        recyclerView = findViewById(R.id.recycler_calendar);
        textMonthYear = findViewById(R.id.text_month_year);
        buttonPreviousMonth = findViewById(R.id.button_previous_month);
        buttonNextMonth = findViewById(R.id.button_next_month);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7)); // 7 columns for days of the week

        // Initialize Calendar
        calendar = Calendar.getInstance();

        // Update the UI
        updateCalendar();

        // Add Click Listeners for Navigation Buttons
        buttonPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1); // Go to previous month
                updateCalendar();
            }
        });

        buttonNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1); // Go to next month
                updateCalendar();
            }
        });
    }

    /**
     * Updates the calendar depending on the month and year
     */
    private void updateCalendar() {
        // Update Month and Year Display
        String month = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);
        textMonthYear.setText(month + ", " + year);

        int todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Generate Days for the Selected Month
        List<String> days = generateDaysForMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

        // Set Adapter
        adapter = new CalendarAdapter(this, days, todayDay, calendar.get(Calendar.MONTH));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Generates the days for the month so that they match the week. Empty slots will be used for days of the week that do not correspond to the
     * given month.
     * @param year
     * @param month
     * @return
     */
    private List<String> generateDaysForMonth(int year, int month) {
        List<String> days = new ArrayList<>();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(year, month, 1);

        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1; // Adjust for 0-based index

        // Add empty slots for days before the first day of the month
        for (int i = 0; i < firstDayOfWeek; i++) {
            days.add(""); // Empty day
        }

        // Add actual days of the month
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(String.valueOf(i));
        }

        return days;
    }
}