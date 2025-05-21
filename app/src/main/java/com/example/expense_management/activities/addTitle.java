package com.example.expense_management.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.adapters.IconGridAdapter;
import com.example.expense_management.api.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class addTitle extends AppCompatActivity {

    private TextInputEditText iconEditText, titleEditText;
    private TextInputLayout iconInputLayout;

    private String[] iconIds = {"lunch_dining_24px", "two_wheeler_24px", "shopping_cart_24px"};
    private int selectedIconIndex = 0;
    private String selectedIconId = "lunch_dining_24px";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_title);

        iconEditText = findViewById(R.id.iconSelectorEditText);
        iconInputLayout = findViewById(R.id.iconInputLayout);
        titleEditText = findViewById(R.id.editTextName); // input người dùng nhập tiêu đề

        iconEditText.setOnClickListener(v -> showIconDialog());

        Button saveButton = findViewById(R.id.savedButton);
        saveButton.setOnClickListener(v -> saveCategory());
    }

    private void showIconDialog() {
        // Lấy tất cả icon từ drawable có hậu tố "_24px"
        Field[] drawables = R.drawable.class.getFields();
        List<String> iconList = new ArrayList<>();

        for (Field field : drawables) {
            String name = field.getName();
            if (name.endsWith("_24px")) {
                iconList.add(name);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");

        // Tạo GridView để hiển thị icon
        GridView gridView = new GridView(this);
        gridView.setNumColumns(3); // 3 cột
        gridView.setAdapter(new IconGridAdapter(this, iconList));

        builder.setView(gridView);

        AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            selectedIconId = iconList.get(position);
            int iconResId = getResources().getIdentifier(selectedIconId, "drawable", getPackageName());
            if (iconResId != 0) {
                Drawable selectedIcon = ContextCompat.getDrawable(this, iconResId);
                iconInputLayout.setStartIconDrawable(selectedIcon);
                iconEditText.setText("Đã chọn icon");
            }
            dialog.dismiss();
        });

        dialog.show();
    }




    private void saveCategory() {
        String title = titleEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        SharedPreferences userPrefs = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        String userId = userPrefs.getString("id", null);

        if (accessToken == null || userId == null) {
            Toast.makeText(this, "Thiếu thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = new ApiService(this, Volley.newRequestQueue(this));
        apiService.addCategory(accessToken, userId, title, selectedIconId,
                () -> {
                    Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(addTitle.this, TitleDetail.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi thêm danh mục: " + error, Toast.LENGTH_SHORT).show();
                    Log.e("ADD_CATEGORY_ERROR", error);
                });
    }

}
