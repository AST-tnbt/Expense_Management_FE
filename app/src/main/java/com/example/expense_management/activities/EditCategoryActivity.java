package com.example.expense_management.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.adapters.IconGridAdapter;
import com.example.expense_management.api.ApiService;
import com.example.expense_management.models.Category;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity {

    private TextInputEditText iconEditText, titleEditText;
    private TextInputLayout iconInputLayout;
    private Button updateBtn;

    private String selectedIconId = "";
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        iconEditText = findViewById(R.id.iconSelectorEditText);
        titleEditText = findViewById(R.id.editTextName);
        iconInputLayout = findViewById(R.id.iconInputLayout);
        updateBtn = findViewById(R.id.updateButton);

        // Nhận category từ Intent
        category = (Category) getIntent().getSerializableExtra("category");
        if (category != null) {
            selectedIconId = getResources().getResourceEntryName(category.getIconId()); // iconId từ drawable name
            titleEditText.setText(category.getName());

            // Hiển thị icon
            int iconResId = getResources().getIdentifier(selectedIconId, "drawable", getPackageName());
            Drawable selectedIcon = ContextCompat.getDrawable(this, iconResId);
            iconInputLayout.setStartIconDrawable(selectedIcon);
            iconEditText.setText(""); // tuỳ, có thể để tên icon
        }

        iconEditText.setOnClickListener(v -> showIconDialog());

        updateBtn.setOnClickListener(v -> updateCategory());
    }

    private void showIconDialog() {
        // Lấy tất cả icon từ drawable có hậu tố "_24px"
        Field[] drawables = R.drawable.class.getFields();
        List<String> iconList = new ArrayList<>();

        for (Field field : drawables) {
            String name = field.getName();
            if (name.endsWith("_category")) {
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


    private void updateCategory() {
        String title = titleEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences tokenPrefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = tokenPrefs.getString("access_token", null);
        SharedPreferences userPrefs = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        String userId = userPrefs.getString("id", null);

        if (accessToken == null || userId == null) {
            Toast.makeText(this, "Thiếu thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.getCateId()==null) {
            Toast.makeText(this, "Thiếu cateid", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService apiService = new ApiService(this, Volley.newRequestQueue(this));
        apiService.updateCategory(accessToken, category.getCateId(), title, selectedIconId, userId,
                () -> {
                    Toast.makeText(this, "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditCategoryActivity.this, TitleDetail.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // hoặc quay về TitleDetail nếu muốn
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi cập nhật: " + error, Toast.LENGTH_SHORT).show();
                    Log.e("UPDATE_CATEGORY_ERROR", error);
                }
        );
    }

}
