package com.example.expense_management.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.adapters.EntryAdapter;
import com.example.expense_management.api.ApiService;
import com.example.expense_management.dtos.CategoryAmount;
import com.example.expense_management.dtos.ExpenseResponse;
import com.example.expense_management.dtos.MonthAmount;
import com.example.expense_management.models.Expense;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseAnalyst extends AppCompatActivity {

    private TextView tvYear, tvMonth;
    private EditText etMonth, etYear;
    private Button btnApply;
    private LinearLayout filterForm;
    private LineChart lineChart;
    private PieChart pieChart;
    private UUID userId;
    private String accessToken;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private EntryAdapter adapter;
    private List<Expense> entries = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_analyst);

        tvYear = findViewById(R.id.tvYearAnalysis);
        tvMonth = findViewById(R.id.tvMonthAnalysis);
        etMonth = findViewById(R.id.etMonth);
        etYear = findViewById(R.id.etYear);
        btnApply = findViewById(R.id.btnApplyFilter);
        filterForm = findViewById(R.id.filterForm);
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);

        SharedPreferences userPrefs = getSharedPreferences("UserStore", MODE_PRIVATE);
        userId = UUID.fromString(userPrefs.getString("id", ""));
        SharedPreferences tokenPrefs = getSharedPreferences("TokenStore", MODE_PRIVATE);
        accessToken = tokenPrefs.getString("access_token", "");
        loadExpenses();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new EntryAdapter(entries, expense -> {
            // Click item -> EditExpense activity
            Intent intent = new Intent(ExpenseAnalyst.this, EditExpense.class);
            intent.putExtra("expenseId", expense.getExpenseId().toString());
            intent.putExtra("category", expense.getTitle());
            intent.putExtra("date", expense.getDate());
            intent.putExtra("amount", expense.getAmount());
            intent.putExtra("cateId", expense.getCateId().toString());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        apiService = new ApiService(this, Volley.newRequestQueue(this));

        tvYear.setOnClickListener(v -> {
            filterForm.setVisibility(View.VISIBLE);
            etMonth.setVisibility(View.GONE);
            etYear.setText("");
            highlightSelected(tvYear, tvMonth);
            btnApply.setOnClickListener(v2 -> {
                String year = etYear.getText().toString();
                if (!year.isEmpty()) {
                    apiService.fetchYearChart(accessToken, userId, year, this::drawLineChart, this::showError);
                }
            });
        });

        tvMonth.setOnClickListener(v -> {
            filterForm.setVisibility(View.VISIBLE);
            etMonth.setVisibility(View.VISIBLE);
            etMonth.setText("");
            etYear.setText("");
            highlightSelected(tvMonth, tvYear);
            btnApply.setOnClickListener(v2 -> {
                String year = etYear.getText().toString();
                String month = etMonth.getText().toString();
                if (!month.isEmpty() && !year.isEmpty()) {
                    apiService.fetchMonthChart(accessToken, userId, month, year, this::drawPieChart, this::showError);
                }
            });
        });
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
    private void highlightSelected(TextView selected, TextView unselected) {
        selected.setTextColor(getResources().getColor(R.color.primaryDark));
        selected.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        unselected.setTextColor(getResources().getColor(R.color.black));
        unselected.setPaintFlags(0);
    }

    private void drawLineChart(List<MonthAmount> data) {
        List<Entry> entries = new ArrayList<>();
        for (MonthAmount item : data) {
            entries.add(new Entry(item.month, (float)item.totalAmount));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Chi tiêu theo tháng");
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.setDrawMarkers(true);
        lineChart.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);
        lineChart.invalidate();
    }

    private void drawPieChart(List<CategoryAmount> data) {

        List<PieEntry> entries = new ArrayList<>();
        for (CategoryAmount item : data) {
            entries.add(new PieEntry((float)item.totalAmount, item.categoryTitle));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(false);
//        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLineColor(Color.BLACK);

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF6600"));
        colors.add(Color.parseColor("#00CC66"));
        colors.add(Color.parseColor("#0000FF"));
        colors.add(Color.parseColor("#FFFF00"));
        colors.add(Color.parseColor("#00FFFF"));
        colors.add(Color.parseColor("#FF00FF"));
        colors.add(Color.parseColor("#FF66CC"));
        colors.add(Color.parseColor("#FF6600"));
        colors.add(Color.parseColor("#CC33FF"));
        colors.add(Color.parseColor("#005500"));

        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(dataSet);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.GONE);
        pieChart.invalidate();
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
