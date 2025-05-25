package com.example.expense_management.dtos;

import java.util.UUID;

public class ExpenseResponse {
    private UUID expenseId;
    private UUID cateId;
    private String date;
    private String spend;
    private String categoryName;
    private String iconId;

    public ExpenseResponse(UUID expenseId,String date, String spend, String categoryName, String iconId,UUID cateId) {
        this.expenseId=expenseId;
        this.date = date;
        this.spend = spend;
        this.categoryName = categoryName;
        this.iconId = iconId;
        this.cateId=cateId;
    }
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

    public UUID getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(UUID expenseId) {
        this.expenseId = expenseId;
    }

    public UUID getCateId() {
        return cateId;
    }

    public void setCateId(UUID cateId) {
        this.cateId = cateId;
    }
}
