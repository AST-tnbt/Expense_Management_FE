package com.example.expense_management.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_management.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpense extends AppCompatActivity {
    private AutoCompleteTextView category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_expense);
        TextInputEditText dateAddInput = findViewById(R.id.dateAddInput);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateAddInput.setText(currentDate);

        category = findViewById(R.id.categoryDropdown);
        String[] categories = new String[]{"Ăn uống", "Mua sắm", "Đi lại"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        category.setAdapter(adapter);

        dateAddInput.setOnClickListener(v -> {
            // Create the date picker with the current date as the default selection
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default to today
                    .build();

            // Show the date picker
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            // Update the TextInputEditText when a date is selected
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Date selectedDate = new Date(selection);
                String formattedDate = dateFormat.format(selectedDate);
                dateAddInput.setText(formattedDate);
            });
        });
    }
}
