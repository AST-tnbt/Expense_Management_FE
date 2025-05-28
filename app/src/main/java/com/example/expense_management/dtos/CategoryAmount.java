package com.example.expense_management.dtos;

public class CategoryAmount {
    public String categoryTitle;
    public double totalAmount;

    public CategoryAmount(String title, double amount) {
        this.categoryTitle = title;
        this.totalAmount = amount;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}