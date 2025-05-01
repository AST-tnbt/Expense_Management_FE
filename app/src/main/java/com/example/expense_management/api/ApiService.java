package com.example.expense_management.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.activities.FragmentActivity;
import com.example.expense_management.activities.MainActivity;

import org.json.JSONException;

public class ApiService {
    private final Context context;
    private final RequestQueue requestQueue;
    private final String baseUrl;

    public ApiService(Context context, RequestQueue requestQueue) {
        this.context = context;
        this.requestQueue = requestQueue;
        this.baseUrl = BuildConfig.BASE_URL;
    }

    public void getInfo(String accessToken) {
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

                        SharedPreferences sharedPreferences = context.getSharedPreferences("UserStore", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("fullName", fullName);
                        editor.putString("email", email);
                        editor.putString("birthDay", dob);
                        editor.putString("gender", gender);
                        editor.putString("id", id);
                        editor.apply();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
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
}

