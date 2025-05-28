package com.example.expense_management.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_management.R;
import com.example.expense_management.models.Expense;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private List<Expense> entries;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }
    public EntryAdapter(List<Expense> entries) {
        this.entries = entries;
    }
    public EntryAdapter(List<Expense> entries, OnItemClickListener listener) {
        this.entries = entries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Expense expense = entries.get(position);
        holder.icon.setImageResource(expense.getIconResId());
        holder.title.setText(expense.getTitle());
        holder.date.setText(expense.getDate());
        holder.amount.setText(expense.getAmount());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(expense);
            }
        });
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
