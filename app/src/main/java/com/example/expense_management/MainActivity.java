package com.example.expense_management;

import android.content.Intent;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
private TextInputEditText editTextEmail, editTextPassword;
    private MaterialButton btnLogin;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        MaterialTextView registerHere = findViewById(R.id.registerHere);

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, register.class);
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


        // Chuẩn bị dữ liệu JSON để gửi lên server
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL API backend
        String url = "http://10.0.2.2:8080/auth/login"; // Nếu bạn dùng Emulator

        // Gửi request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    // Có thể chuyển sang màn hình khác tại đây nếu cần
                },
                error -> {
                    Toast.makeText(this, "Lỗi khi đăng nhập: " + error.toString(), Toast.LENGTH_LONG).show();
                    Log.e("LoginError", "Lỗi khi đăng nhập", error);
                }
        );

        // Thêm request vào queue
        requestQueue.add(jsonObjectRequest);
    }

}