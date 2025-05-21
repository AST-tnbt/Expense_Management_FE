package com.example.expense_management.dtos;

public class ExpenseResponse {
    private String date;
    private String spend;
    private String categoryName;
    private String iconId;

    public ExpenseResponse(String date, String spend, String categoryName, String iconId) {
        this.date = date;
        this.spend = spend;
        this.categoryName = categoryName;
        this.iconId = iconId;
    }

    public String getDate() { return date; }
    public String getSpend() { return spend; }
    public String getCategoryName() { return categoryName; }
    public String getIconId() { return iconId; }
}
