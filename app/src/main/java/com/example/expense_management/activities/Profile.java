package com.example.expense_management.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Profile extends Fragment {
    private MaterialButton saveBtn, logoutBtn;
    private RequestQueue requestQueue;
    private String baseUrl;

    public Profile() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        baseUrl = BuildConfig.BASE_URL;
        Context context = requireContext();
        SharedPreferences prefs = context.getSharedPreferences("UserStore", Context.MODE_PRIVATE);

        saveBtn = view.findViewById(R.id.btnUpdate);
        logoutBtn = view.findViewById(R.id.btnLogout);
        TextInputEditText editTextName = view.findViewById(R.id.editTextName);
        TextInputEditText editTextEmail = view.findViewById(R.id.editTextEmail);
        TextInputEditText dateOfBirthInput = view.findViewById(R.id.dateOfBirthInput);
        AutoCompleteTextView genderDropdown = view.findViewById(R.id.genderDropdown);
        MaterialButton btnChangepass = view.findViewById(R.id.btnChangePass);
        if (btnChangepass != null) {
            btnChangepass.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), ChangePassword.class);
                startActivity(intent);
            });
        } else {
            Log.e("ProfileFragment", "btnChangePass is null");
        }

        String name = prefs.getString("fullName", "N/A");
        String email = prefs.getString("email", "N/A");
        String dob = prefs.getString("birthDay", "N/A");
        String gender = prefs.getString("gender", "N/A");

        String[] genders = new String[]{"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, genders);
        genderDropdown.setAdapter(adapter);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                        .build())
                .build();

        dateOfBirthInput.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = sdf.format(new Date(selection));
            dateOfBirthInput.setText(selectedDate);
        });

        genderDropdown.setText(gender, false);

        editTextName.setText(name);
        editTextEmail.setText(email);
        dateOfBirthInput.setText(dob);

        saveBtn.setOnClickListener(v -> {
            String username = Objects.requireNonNull(editTextName.getText()).toString().trim();
            String emailVal = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
            String dobVal = Objects.requireNonNull(dateOfBirthInput.getText()).toString().trim();
            String genderVal = genderDropdown.getText().toString().trim();

            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn cập nhật thông tin?")
                    .setPositiveButton("Cập nhật", (dialog, which) -> {
                        SharedPreferences tokenStore = requireContext().getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
                        String accessToken = tokenStore.getString("access_token", null);
                        String userId = prefs.getString("id", "");

                        JSONObject requestBody = new JSONObject();
                        try {
                            requestBody.put("fullName", username);
                            requestBody.put("email", emailVal);
                            requestBody.put("birthDay", dobVal);
                            requestBody.put("gender", genderVal);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String url = baseUrl + "/users/" + userId;

                        JsonObjectRequest updateRequest = new JsonObjectRequest(
                                Request.Method.PUT,
                                url,
                                requestBody,
                                response -> {
                                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("fullName", username);
                                    editor.putString("email", emailVal);
                                    editor.putString("birthDay", dobVal);
                                    editor.putString("gender", genderVal);
                                    editor.apply();
                                },
                                error -> {
                                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
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

                        requestQueue.add(updateRequest);
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });



        requestQueue = Volley.newRequestQueue(context);
        logoutBtn.setOnClickListener(v -> showLogoutConfirmDialog());

    }

    private void logout(String accessToken, String refreshToken) {
        String url = baseUrl + "/logout";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("refreshToken", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        String message = response.getString("message");
                        if (message.equals("Logout successfully")) {
                            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
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

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    SharedPreferences tokenStore = requireContext().getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
                    String accessToken = tokenStore.getString("access_token", null);
                    String refreshToken = tokenStore.getString("refresh_token", null);
                    logout(accessToken, refreshToken);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
