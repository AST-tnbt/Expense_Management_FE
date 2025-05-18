package com.example.expense_management.dtos;

import java.util.List;

public interface CategoriesCallback {
    void onSuccess(List<CategoriesResponse> categories);
    void onError(String error);
}
