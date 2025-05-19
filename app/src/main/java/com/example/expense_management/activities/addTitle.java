package com.example.expense_management.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

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
        String[] iconNames = {"Ăn uống", "Di chuyển", "Mua sắm"};
        String[] iconIds = {"lunch_dining_24px", "two_wheeler_24px", "shopping_cart_24px"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn icon");

        builder.setItems(iconNames, (dialog, which) -> {
            selectedIconId = iconIds[which]; // lưu tên iconId

            int iconResId = getResources().getIdentifier(
                    selectedIconId, "drawable", getPackageName());

            if (iconResId != 0) {
                Drawable selectedIcon = ContextCompat.getDrawable(this, iconResId);
                iconInputLayout.setStartIconDrawable(selectedIcon);
                iconEditText.setText(iconNames[which]); // hiển thị tên người dùng dễ hiểu
            }
        });

        builder.show();
    }


    private void saveCategory() {
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
            body.put("title", title);
            body.put("iconId", selectedIconId);  // <== SỬ DỤNG ICON TỪ DRAWABLE
            body.put("userId", userId);

            String url = "http://10.0.2.2:8000/categories";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        Log.e("API_ERROR", error.toString());
                        Toast.makeText(this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
