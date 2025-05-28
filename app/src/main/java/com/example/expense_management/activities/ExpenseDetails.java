package com.example.expense_management.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.adapters.EntryAdapter;
import com.example.expense_management.api.ApiService;
import com.example.expense_management.dtos.ExpenseResponse;
import com.example.expense_management.models.Expense;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EntryAdapter adapter;
    private List<Expense> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details); // dùng layout của Activity

        recyclerView = findViewById(R.id.expenseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadExpenses();
        adapter = new EntryAdapter(entries, expense -> {
            Intent intent = new Intent(ExpenseDetails.this,EditExpense.class);
          intent.putExtra("expenseId", expense.getExpenseId().toString());
            intent.putExtra("category", expense.getTitle());
            intent.putExtra("date", expense.getDate());
            intent.putExtra("amount", expense.getAmount());
            intent.putExtra("cateId", expense.getCateId().toString());

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

    }

    private void loadExpenses() {
        SharedPreferences userPrefs = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        String userIdStr = userPrefs.getString("id", null);
        SharedPreferences tokenPrefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = tokenPrefs.getString("access_token", null);

        if (userIdStr == null || accessToken == null) {
            Toast.makeText(this, "Thiếu thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        UUID userId = UUID.fromString(userIdStr);
        ApiService apiService = new ApiService(this, Volley.newRequestQueue(this));

        apiService.fetchExpensesByUserId(
                accessToken,
                userId,
                expenseResponses -> {
                    entries.clear();
                    for (ExpenseResponse res : expenseResponses) {
                        int iconResId = getResources().getIdentifier(
                                res.getIconId(), "drawable", getPackageName());
                        if (iconResId == 0) iconResId = R.drawable.cake_24px;

                        entries.add(new Expense(res.getExpenseId(),iconResId, res.getCategoryName(), res.getDate(), res.getSpend() + " VNĐ",res.getCateId()));
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                },
                error -> {
                    Log.e("ExpenseLoadError", error);
                    Toast.makeText(this, "Lỗi khi tải dữ liệu chi tiêu", Toast.LENGTH_SHORT).show();
                }
        );
    }
}
