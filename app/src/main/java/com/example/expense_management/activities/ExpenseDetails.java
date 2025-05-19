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

import com.example.expense_management.R;
import com.example.expense_management.adapters.EntryAdapter;
import com.example.expense_management.api.ApiService;
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
        SharedPreferences tokenStore = requireContext().getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String userIdString = tokenStore.getString("id", null);

        if (userIdString != null) {
            UUID userId = UUID.fromString(userIdString);

            ApiService.fetchExpensesByUserId(requireContext(), userId, new ApiService.ExpenseCallback() {
                @Override
                public void onSuccess(JSONArray expenseList) {
                    entries.clear();
                    for (int i = 0; i < expenseList.length(); i++) {
                        try {
                            JSONObject obj = expenseList.getJSONObject(i);
                            String date = obj.getString("date");
                            String spend = obj.getString("spend");
                            String category = obj.getString("categoryName");
                            String iconIdStr = obj.getString("iconId"); // ví dụ: "lunch_dining_24px"

                            int iconResId = getResources().getIdentifier(
                                    iconIdStr, "drawable", requireContext().getPackageName());

                            if (iconResId == 0) {
                                iconResId = R.drawable.cake_24px;
                            }

                            entries.add(new Entry(iconResId, category, date, spend + " VNĐ"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("API_ERROR", errorMessage);
                    Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu chi tiêu", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(requireContext(), "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
        }
    }
}
