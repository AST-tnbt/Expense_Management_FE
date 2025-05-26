package com.example.expense_management.activities;





import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.api.ApiService;
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

public class EditExpense extends AppCompatActivity {

    private TextInputEditText dateAddInput, amountInput;
    private AutoCompleteTextView categoryDropdown;
    private MaterialButton editBtn, deleteBtn;
    private List<CategoriesResponse> categoriesList;
    private String cateId, expenseId;
    private ApiService apiService;
    private String accessToken, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_expense);

        dateAddInput = findViewById(R.id.dateAddInput);
        amountInput = findViewById(R.id.amountInput);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        // Định dạng ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Lấy token và userId
        SharedPreferences prefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        accessToken = prefs.getString("access_token", null);
        SharedPreferences userPrefs = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        userId = userPrefs.getString("id", null);

        apiService = new ApiService(this, Volley.newRequestQueue(this));

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String expenseIdStr = getIntent().getStringExtra("expenseId");
        UUID expenseId = UUID.fromString(expenseIdStr);
        String date = intent.getStringExtra("date");
        String spend = intent.getStringExtra("amount");
        String categoryName = intent.getStringExtra("category");
       cateId = getIntent().getStringExtra("cateId");
        Log.d("EditExpenseDebug", "Selected cateId: " + cateId);
        Log.d("EditExpenseDebug2", "Selected expenseId: " + expenseId);

        dateAddInput.setText(date);
        amountInput.setText(spend);
        categoryDropdown.setText(categoryName, false);

        // Lấy danh mục
        apiService.fetchCategoriesByUserId(
                accessToken,
                java.util.UUID.fromString(userId),
                categories -> {
                    categoriesList = categories;
                    List<String> titles = new ArrayList<>();
                    for (CategoriesResponse cat : categories) {
                        titles.add(cat.getTitle());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            titles
                    );
                    categoryDropdown.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Lỗi khi tải danh mục: " + error, Toast.LENGTH_SHORT).show()
        );

        // Sự kiện chọn danh mục
        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            CategoriesResponse selectedCategory = categoriesList.get(position);
            cateId = selectedCategory.getCateId().toString();
        });

        // Sự kiện chọn ngày
        dateAddInput.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Chọn ngày")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Date selectedDate = new Date(selection);
                String formattedDate = dateFormat.format(selectedDate);
                dateAddInput.setText(formattedDate);
            });
        });
        Button addCateBtn = findViewById(R.id.addCateBtn);

        addCateBtn.setOnClickListener(v -> {
            Intent intentAddCate = new Intent(EditExpense.this, AddTitle.class);
            startActivity(intentAddCate);
        });
        // Sự kiện sửa
        editBtn.setOnClickListener(v -> {
            String dateInput = Objects.requireNonNull(dateAddInput.getText()).toString().trim();
            String amountText = Objects.requireNonNull(amountInput.getText()).toString().trim();

            if (dateInput.isEmpty() || amountText.isEmpty() || cateId == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String cleanedAmount = amountText.replaceAll("[^\\d.]", ""); // Chỉ giữ số và dấu chấm
            BigDecimal amount;
            try {
                amount = new BigDecimal(cleanedAmount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format lại ngày về yyyy-MM-dd
            String formattedDate = null;
            String[] possibleFormats = {"dd/MM/yyyy", "yyyy-MM-dd", "MM-dd-yyyy"};

            for (String format : possibleFormats) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                    Date parsedDate = inputFormat.parse(dateInput);
                    // Nếu parse thành công thì thoát vòng lặp
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    formattedDate = outputFormat.format(parsedDate);
                    break;
                } catch (Exception ignored) {
                    // thử định dạng tiếp theo
                }
            }

            if (formattedDate == null) {
                Toast.makeText(this, "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }


            apiService.updateExpense(accessToken, expenseId, userId, formattedDate, amount, cateId,
                    () -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(EditExpense.this, ExpenseDetails.class);
                        startActivity(intent1);
                        finish();
                    },
                    error -> {
                        Log.e("EditExpenseError", "Lỗi cập nhật: " + error);
                        Toast.makeText(this, "Lỗi cập nhật: " + error, Toast.LENGTH_SHORT).show();
                    }
            );
        });

        // Sự kiện xóa
        deleteBtn.setOnClickListener(v -> {
            apiService.deleteExpense(accessToken, expenseId,
                    response -> {
                        Toast.makeText(this, "Đã xóa chi tiêu", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        Log.e("DeleteExpenseError", "Lỗi xóa: " + error);
                        Toast.makeText(this, "Lỗi xóa: " + error, Toast.LENGTH_SHORT).show();
                    }
            );
        });

    }
}
