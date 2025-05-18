package com.example.expense_management.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.api.ApiService;
import com.example.expense_management.dtos.CategoriesCallback;
import com.example.expense_management.dtos.CategoriesResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class AddExpense extends AppCompatActivity {
    private TextInputEditText dateAddInput;
    private TextInputEditText amountInput;
    private AutoCompleteTextView category;
    private List<CategoriesResponse> categoriesList;
    private MaterialButton addBtn;
    private String cateId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_expense);
        dateAddInput = findViewById(R.id.dateAddInput);
        amountInput = findViewById(R.id.amountInput);
        addBtn = findViewById(R.id.addBtn);
        cateId = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateAddInput.setText(currentDate);

        category = findViewById(R.id.categoryDropdown);
        SharedPreferences prefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        ApiService apiService = new ApiService(this, Volley.newRequestQueue(this));

        apiService.getAllCategories(accessToken, new CategoriesCallback() {
            @Override
            public void onSuccess(List<CategoriesResponse> categories) {
                categoriesList = categories;
                List<String> titles = new ArrayList<>();
                for (CategoriesResponse cat : categories) {
                    titles.add(cat.getTitle());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddExpense.this,
                        android.R.layout.simple_dropdown_item_1line, titles);
                category.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddExpense.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        category.setOnItemClickListener((parent, view, position, id) -> {
            CategoriesResponse selectedCategory = categoriesList.get(position);
            // Assuming getId() returns the ID
            cateId = selectedCategory.getCateId().toString();
        });

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
        addBtn.setOnClickListener(v -> {
           addExpense();
        });
    }
    private void addExpense() {
        SharedPreferences userPrefs = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        String dateInput = Objects.requireNonNull(dateAddInput.getText()).toString().trim();
        String amountText = Objects.requireNonNull(amountInput.getText()).toString().trim();
        String userId = userPrefs.getString("id", null);

        if (dateInput.isEmpty() || amountText.isEmpty() || cateId == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reformat date to yyyy-MM-dd if needed
        String formattedDate;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(dateInput);
            formattedDate = outputFormat.format(parsedDate);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get token
        SharedPreferences prefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        ApiService apiService = new ApiService(this, Volley.newRequestQueue(this));
        apiService.addExpense(accessToken, userId, formattedDate, amount, cateId,
                () -> {
                    Toast.makeText(this, "Đã thêm chi tiêu", Toast.LENGTH_SHORT).show();
                    clearForm();
                },
                error -> Toast.makeText(this, "Add failed: " + error, Toast.LENGTH_SHORT).show()
        );
    }
    private void clearForm() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateAddInput.setText(dateFormat.format(new Date()));
        amountInput.setText("");
        category.setText("", false); // Clear text without triggering dropdown
        cateId = null;
    }
}
