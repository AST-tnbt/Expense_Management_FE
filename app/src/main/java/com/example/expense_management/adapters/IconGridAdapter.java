package com.example.expense_management.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class IconGridAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> iconList;

    public IconGridAdapter(Context context, List<String> iconList) {
        this.context = context;
        this.iconList = iconList;
    }

    @Override
    public int getCount() {
        return iconList.size();
    }

    @Override
    public Object getItem(int position) {
        return iconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        int resId = context.getResources().getIdentifier(iconList.get(position), "drawable", context.getPackageName());
        Drawable icon = ContextCompat.getDrawable(context, resId);
        imageView.setImageDrawable(icon);

        int size = (int) (80 * context.getResources().getDisplayMetrics().density); // ~80dp
        imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        imageView.setPadding(16, 16, 16, 16);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        return imageView;
    }
}
