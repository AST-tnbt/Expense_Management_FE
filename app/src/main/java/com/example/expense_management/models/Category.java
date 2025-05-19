package com.example.expense_management.models;

import java.io.Serializable;
import java.util.UUID;

public class Category implements Serializable {
    private UUID cateId;
    private int iconId;
    private String name;

    public Category(UUID cateId,int iconId, String name) {
        this.cateId=cateId;
        this.iconId = iconId;
        this.name = name;
    }
    public Category(int iconId, String name) {
        this.iconId = iconId;
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public String getName() {
        return name;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCateId() {
        return cateId;
    }

    public void setCateId(UUID cateId) {
        this.cateId = cateId;
    }
}
