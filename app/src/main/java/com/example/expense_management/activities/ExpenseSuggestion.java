package com.example.expense_management.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.api.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.util.UUID;

public class ExpenseSuggestion extends AppCompatActivity {
    private TextInputEditText expenseTarget;
    private MaterialButton suggestBtn;
    private TextView suggestText;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_suggestion);
        expenseTarget = findViewById(R.id.amountInput);
        suggestBtn = findViewById(R.id.suggestButton);
        suggestText = findViewById(R.id.suggestContent);
        suggestText.setMovementMethod(new android.text.method.ScrollingMovementMethod());

        apiService = new ApiService(this, Volley.newRequestQueue(this));
        CircularProgressIndicator loading = findViewById(R.id.loading);
        suggestBtn.setOnClickListener(v -> {
            SharedPreferences userPrefs = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
            String userIdStr = userPrefs.getString("id", null);
            UUID userId = UUID.fromString(userIdStr);
            SharedPreferences tokenPrefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
            String accessToken = tokenPrefs.getString("access_token", null);
            String targetStr = expenseTarget.getText() != null ? expenseTarget.getText().toString() : "";
            BigDecimal targetValue;

            try {
                targetValue = new BigDecimal(targetStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            loading.setVisibility(View.VISIBLE);
            apiService.fetchSuggestion(accessToken, userId, targetValue,
                    suggest -> {
                        loading.setVisibility(View.GONE);
                        suggestText.setText(suggest);
                    },
                    (statusCode, errorMessage) -> {
                        loading.setVisibility(View.GONE);
                        if (statusCode == 429) {
                            Toast.makeText(this, "Quá nhiều yêu cầu, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

}
