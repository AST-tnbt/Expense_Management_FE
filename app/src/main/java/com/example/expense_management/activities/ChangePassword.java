package com.example.expense_management.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangePassword extends AppCompatActivity {
    private TextInputEditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private MaterialButton changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordInput = findViewById(R.id.editTextPassword);
        newPasswordInput = findViewById(R.id.editNewTextPassword);
        confirmPasswordInput = findViewById(R.id.editTextConfirm_Password);
        changePasswordBtn = findViewById(R.id.btnChange);

        changePasswordBtn.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String currentPassword = Objects.requireNonNull(currentPasswordInput.getText()).toString().trim();
        String newPassword = Objects.requireNonNull(newPasswordInput.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi request đổi mật khẩu
        changePassword(currentPassword, newPassword);
    }

    private void changePassword(String currentPassword, String newPassword) {
        SharedPreferences tokenStore = getSharedPreferences("TokenStore", MODE_PRIVATE);
        String accessToken = tokenStore.getString("access_token", null);
        String refreshToken = tokenStore.getString("refresh_token", null);

        String url = BuildConfig.BASE_URL + "/users/change-password";

        StringRequest changePassRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "Đổi mật khẩu thành công, đang đăng xuất...", Toast.LENGTH_SHORT).show();
                    performLogout(refreshToken); // Gọi logout sau khi đổi mật khẩu thành công
                },
                error -> {
                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data);
                        Log.e("ChangePassword", "Lỗi đổi mật khẩu: " + responseBody);
                        Toast.makeText(this, "Đổi mật khẩu thất bại: " + responseBody, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("ChangePassword", "Lỗi mạng hoặc server không phản hồi: " + error.getMessage());
                        Toast.makeText(this, "Lỗi mạng hoặc server không phản hồi", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                JSONObject json = new JSONObject();
                try {
                    json.put("currentPassword", currentPassword);
                    json.put("newPassword", newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(changePassRequest);
    }


    private void performLogout(String refreshToken) {
        String url = BuildConfig.BASE_URL + "/logout";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("refreshToken", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest logoutRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    SharedPreferences tokenStore = getSharedPreferences("TokenStore", MODE_PRIVATE);
                    tokenStore.edit().clear().apply();

                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                },
                error -> {
                    Toast.makeText(this, "Đã đổi mật khẩu nhưng lỗi khi đăng xuất", Toast.LENGTH_SHORT).show();

                    // Vẫn xoá token và chuyển về Main
                    SharedPreferences tokenStore = getSharedPreferences("TokenStore", MODE_PRIVATE);
                    tokenStore.edit().clear().apply();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(logoutRequest);
    }
}