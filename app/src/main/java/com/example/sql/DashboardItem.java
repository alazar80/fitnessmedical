package com.example.sql;

import androidx.annotation.DrawableRes;

public class DashboardItem {
    private final String title;
    private final int value;
    private final @DrawableRes int iconRes;

    public DashboardItem(String title, int value, @DrawableRes int iconRes) {
        this.title   = title;
        this.value   = value;
        this.iconRes = iconRes;
    }
    public String getTitle()   { return title; }
    public int    getValue()   { return value; }
    public int    getIconRes() { return iconRes; }
}
