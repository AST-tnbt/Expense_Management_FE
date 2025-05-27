package com.example.expense_management.models;

import java.util.UUID;

public class Expense {
    private UUID ExpenseId;
    private int iconResId;
    private String title;
    private String date;
    private String amount;
    private UUID cateId;

    public Expense(UUID expenseId, int iconResId, String title, String date, String amount, UUID cateId) {
        ExpenseId = expenseId;
        this.iconResId = iconResId;
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.cateId=cateId;
    }

    public Expense(int iconResId, String title, String date, String amount) {
        this.iconResId = iconResId;
        this.title = title;
        this.date = date;
        this.amount = amount;
    }



    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public UUID getExpenseId() {
        return ExpenseId;
    }

    public void setExpenseId(UUID expenseId) {
        ExpenseId = expenseId;
    }

    public UUID getCateId() {
        return cateId;
    }

    public void setCateId(UUID cateId) {
        this.cateId = cateId;
    }
}
