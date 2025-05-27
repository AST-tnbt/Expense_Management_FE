package com.example.expense_management.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.R;
import com.example.expense_management.api.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private MaterialButton btnLogin;
    private RequestQueue requestQueue;
    private String baseUrl;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        prefs = getSharedPreferences("TokenStore", MODE_PRIVATE);
        MaterialTextView registerHere = findViewById(R.id.registerHere);
        baseUrl = BuildConfig.BASE_URL;
        String accessToken = prefs.getString("access_token", null);
        String refreshToken = prefs.getString("refresh_token", null);
        requestQueue = Volley.newRequestQueue(this);

        if (accessToken != null && refreshToken != null) {
            getInfo(accessToken, refreshToken);
        }

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        requestQueue = Volley.newRequestQueue(this);
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> signInUser() );

    }

    private void signInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = baseUrl + "/auth/login";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        prefs = getSharedPreferences("TokenStore", MODE_PRIVATE);
                        String accessToken = response.getString("accessToken");
                        String refreshToken = response.getString("refreshToken");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", refreshToken);
                        editor.apply();
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        getUserInfoAndNavigate(accessToken);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi đăng nhập: " + error.toString(), Toast.LENGTH_LONG).show();
                    Log.e("LoginError", "Lỗi khi đăng nhập", error);
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void getUserInfoAndNavigate(String accessToken) {
        String url = baseUrl + "/users/me";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String fullName = response.getString("fullName");
                        String email = response.getString("email");
                        String dob = response.getString("birthDay");
                        String gender = response.getString("gender");
                        String id = response.getString("id");

                        SharedPreferences sharedPreferences = getSharedPreferences("UserStore", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("email", email);
                        editor.putString("birthDay", dob);
                        editor.putString("gender", gender);
                        editor.putString("id", id);
                        editor.apply();

                        // Now that we have saved the user info, navigate to the main app
                        Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi khi xử lý thông tin người dùng", Toast.LENGTH_SHORT).show();
                        Log.e("UserInfoError", "Lỗi khi xử lý thông tin người dùng", e);
                    }
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    Log.e("UserInfoError", "Lỗi khi lấy thông tin người dùng", error);
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void getInfo(String accessToken, String refreshToken) {
        String url = baseUrl + "/users/me";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String fullName = response.getString("fullName");
                        String email = response.getString("email");
                        String dob = response.getString("birthDay");
                        String gender = response.getString("gender");
                        String id = response.getString("id");

                        SharedPreferences sharedPreferences = getSharedPreferences("UserStore", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("email", email);
                        editor.putString("birthDay", dob);
                        editor.putString("gender", gender);
                        editor.putString("id", id);
                        editor.apply();

                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    refreshTokens(refreshToken);
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    
    private void refreshTokens(String refreshToken) {
        String url = baseUrl + "/refresh";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String accessToken = response.getString("accessToken");
                        String newRefreshToken = response.getString("refreshToken");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", newRefreshToken);
                        editor.apply();
                        getInfo(accessToken, newRefreshToken);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserStore", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + refreshToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}