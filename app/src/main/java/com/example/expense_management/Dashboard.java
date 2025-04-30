package com.example.expense_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_management.adapters.EntryAdapter;
import com.example.expense_management.models.Entry;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Arrays;
import java.util.List;

public class Dashboard extends AppCompatActivity {
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dashboard);
        RecyclerView recyclerView = findViewById(R.id.transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);

        List<Entry> entries = Arrays.asList(
                new Entry(R.drawable.lunch_dining_24px, "Ăn uống", "20 Feb 2024", "200.000 VNĐ"),
                new Entry(R.drawable.two_wheeler_24px, "Đi lại", "13 Mar 2024", "50.000 VNĐ"),
                new Entry(R.drawable.shopping_cart_24px, "Mua sắm", "11 Mar 2024", "100.000 VNĐ")
        );

        EntryAdapter adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, AddExpense.class);
            startActivity(intent);
        });
    }
}
