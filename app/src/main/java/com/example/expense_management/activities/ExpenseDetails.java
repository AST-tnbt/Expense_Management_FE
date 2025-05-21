package com.example.expense_management.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseDetails extends Fragment {

    private RecyclerView recyclerView;
    private EntryAdapter adapter;
    private List<Entry> entries = new ArrayList<>();

    public ExpenseDetails() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_expense_details, container, false);

        recyclerView = view.findViewById(R.id.expenseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);

        loadExpenses();

        return view;
    }

    private void loadExpenses() {
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

        apiService.fetchExpensesByUserId(
                accessToken,
                userId,
                expenseResponses -> {
                    entries.clear();
                    for (ExpenseResponse res : expenseResponses) {
                        int iconResId = getResources().getIdentifier(
                                res.getIconId(), "drawable", requireContext().getPackageName());
                        if (iconResId == 0) iconResId = R.drawable.cake_24px;

                        entries.add(new Entry(iconResId, res.getCategoryName(), res.getDate(), res.getSpend() + " VNĐ"));
                    }
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                },
                error -> {
                    Log.e("ExpenseLoadError", error);
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu chi tiêu", Toast.LENGTH_SHORT).show();
                }
        );
    }

}
