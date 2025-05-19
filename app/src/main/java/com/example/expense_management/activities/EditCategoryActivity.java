package com.example.expense_management.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.models.Category;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

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
        String[] iconNames = {"Ăn uống", "Di chuyển", "Mua sắm"};
        String[] iconIds = {"lunch_dining_24px", "two_wheeler_24px", "shopping_cart_24px"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn icon");

        builder.setItems(iconNames, (dialog, which) -> {
            selectedIconId = iconIds[which];
            int iconResId = getResources().getIdentifier(selectedIconId, "drawable", getPackageName());
            Drawable selectedIcon = ContextCompat.getDrawable(this, iconResId);
            iconInputLayout.setStartIconDrawable(selectedIcon);
            iconEditText.setText(iconNames[which]);
        });

        builder.show();
    }

    private void updateCategory() {
        String title = titleEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);

        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("cateId", category.getCateId()); // ID danh mục cần sửa
            body.put("title", title);
            body.put("iconId", selectedIconId);
            body.put("userId", userId);

            String url = "http://10.0.2.2:8000/categories"; // API PUT endpoint

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    body,
                    response -> {
                        Toast.makeText(this, "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        Log.e("API_ERROR", error.toString());
                        Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
