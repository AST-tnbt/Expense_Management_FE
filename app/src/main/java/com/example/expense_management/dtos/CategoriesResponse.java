package com.example.expense_management.dtos;

import java.util.UUID;

public class CategoriesResponse {
    private UUID cateId;
    private String title;
    private String iconId;

    public CategoriesResponse(UUID cateId, String title, String iconId) {
        this.cateId = cateId;
        this.title = title;
        this.iconId = iconId;
    }

    public UUID getCateId() {
        return cateId;
    }

    public void setCateId(UUID cateId) {
        this.cateId = cateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }
}
