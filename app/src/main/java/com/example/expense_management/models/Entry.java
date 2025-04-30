package com.example.expense_management.models;

public class Entry {
    private int iconResId;
    private String title;
    private String date;
    private String amount;

    public Entry(int iconResId, String title, String date, String amount) {
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
}
