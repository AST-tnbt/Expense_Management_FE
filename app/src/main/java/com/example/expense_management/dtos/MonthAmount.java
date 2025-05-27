package com.example.expense_management.dtos;

public class MonthAmount {
    public int month;
    public double totalAmount;

    public MonthAmount(int month, double totalAmount) {
        this.month = month;
        this.totalAmount = totalAmount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}