package com.example.expense_management.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_management.R;
import com.example.expense_management.models.Entry;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private List<Entry> entries;

    public EntryAdapter(List<Entry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.icon.setImageResource(entry.getIconResId());
        holder.title.setText(entry.getTitle());
        holder.date.setText(entry.getDate());
        holder.amount.setText(entry.getAmount());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, date, amount, vatPayment;

        public EntryViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.entry_icon);
            title = itemView.findViewById(R.id.entry_title);
            date = itemView.findViewById(R.id.entry_date);
            amount = itemView.findViewById(R.id.entry_amount);
        }
    }
}
