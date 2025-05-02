package com.example.expense_management.configs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_management.R;
import com.example.expense_management.adapters.EntryAdapter;
import com.example.expense_management.models.Entry;

import java.util.Arrays;
import java.util.List;

public class Dashboard extends Fragment {
    public Dashboard() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Entry> entries = Arrays.asList(
                new Entry(R.drawable.lunch_dining_24px, "Ăn uống", "20 Feb 2024", "200.000 VNĐ"),
                new Entry(R.drawable.two_wheeler_24px, "Đi lại", "13 Mar 2024", "50.000 VNĐ"),
                new Entry(R.drawable.shopping_cart_24px, "Mua sắm", "11 Mar 2024", "100.000 VNĐ")
        );

        EntryAdapter adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
