package com.example.expense_management.dtos;

import java.util.List;

public interface ExpenseCallback {
    void onSuccess(List<ExpenseResponse> expenses);
    void onError(String error);
}
