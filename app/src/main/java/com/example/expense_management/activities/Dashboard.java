package com.example.expense_management.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.adapters.EntryAdapter;
import com.example.expense_management.api.ApiService;
import com.example.expense_management.dtos.ExpenseResponse;
import com.example.expense_management.models.Entry;
import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Dashboard extends Fragment {
    public Dashboard() {}
    private RecyclerView recyclerView;
    private EntryAdapter adapter;
    private List<Entry> entries = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard, container, false);

        recyclerView = view.findViewById(R.id.transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntryAdapter(entries, entry -> {
            Intent intent = new Intent(getContext(), EditExpense.class);
            intent.putExtra("expenseId", entry.getExpenseId().toString());
            intent.putExtra("category", entry.getTitle());
            intent.putExtra("date", entry.getDate());
            intent.putExtra("amount", entry.getAmount());
            intent.putExtra("cateId", entry.getCateId().toString());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        SharedPreferences userPrefs = requireContext().getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        String userIdStr = userPrefs.getString("id", null);
        SharedPreferences tokenPrefs = requireContext().getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = tokenPrefs.getString("access_token", null);
        ApiService apiService = new ApiService(requireContext(), Volley.newRequestQueue(requireContext()));

        TextView sumSpendText = view.findViewById(R.id.sumSpend);
        
        // Check if userIdStr is null before trying to convert it to UUID
        if (userIdStr == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            // Redirect to login screen
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return view;
        }
        
        UUID userId = UUID.fromString(userIdStr);

        // Gọi API để lấy tổng chi tiêu tháng hiện tại
        apiService.getMonthlyExpenseTotal(accessToken, userId,
                total -> {
                    String formatted = String.format("%,.0f VNĐ", total.doubleValue());
                    sumSpendText.setText(formatted);
                },
                errorMessage -> {
                    sumSpendText.setText("Không thể tải dữ liệu");
                    Log.e("MonthlyTotalError", errorMessage);
                });


        loadRecentExpenses(); // gọi API



        /*xử lí click*/
        MaterialCardView layoutChiTiet = view.findViewById(R.id.cardExpense);
        layoutChiTiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ExpenseDetails.class);
                startActivity(intent);
            }
        });
        MaterialCardView cardLayout= view.findViewById(R.id.cardTarget);
        cardLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(requireContext(),TitleDetail.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void loadRecentExpenses() {
        SharedPreferences userPrefs = requireContext().getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        String userIdStr = userPrefs.getString("id", null);
        SharedPreferences tokenPrefs = requireContext().getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = tokenPrefs.getString("access_token", null);

        if (userIdStr == null || accessToken == null) {
            Toast.makeText(getContext(), "Thiếu thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        UUID userId = UUID.fromString(userIdStr);
        ApiService apiService = new ApiService(requireContext(), Volley.newRequestQueue(requireContext()));

        apiService.fetchRecentExpenses(
                accessToken,
                userId,
                expenseResponses -> {
                    entries.clear();
                    DecimalFormat formatter = new DecimalFormat("#,###.##");
                    for (ExpenseResponse res : expenseResponses) {
                        int iconResId = getResources().getIdentifier(
                                res.getIconId(), "drawable", requireContext().getPackageName());
                        if (iconResId == 0) iconResId = R.drawable.cake_24px;
                        double spendValue = Double.parseDouble(res.getSpend());
                        entries.add(new Entry(
                                res.getExpenseId(),
                                iconResId,
                                res.getCategoryName(),
                                res.getDate(),
                                formatter.format(spendValue) + " VNĐ",
                                res.getCateId()
                        ));
                    }
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                },
                error -> {
                    Log.e("DashboardLoadError", error);
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
        );

    }


}
