package com.example.expense_management.api;

import static com.example.expense_management.BuildConfig.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.BuildConfig;
import com.example.expense_management.activities.FragmentActivity;
import com.example.expense_management.activities.MainActivity;
import com.example.expense_management.dtos.CategoriesCallback;
import com.example.expense_management.dtos.CategoriesResponse;
import com.example.expense_management.dtos.ExpenseResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import java.util.UUID;

public class ApiService {
    private final Context context;
    private final RequestQueue requestQueue;
    private final String baseUrl;

    public ApiService(Context context, RequestQueue requestQueue) {
        this.context = context;
        this.requestQueue = requestQueue;
        this.baseUrl = BASE_URL;
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    public interface ExpenseCallback {
        void onSuccess(JSONArray expenseList);
        void onError(String errorMessage);
    }

    public void fetchExpensesByUserId(
            String accessToken,
            UUID userId,
            SuccessListenerExpense successCallback,
            ErrorListenerExpense errorCallback
    ) {
        String url = baseUrl + "/expenses/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<ExpenseResponse> expenseList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String expenseStr = obj.getString("expenseId");
                            UUID expenseId = UUID.fromString(expenseStr);
                            String date = obj.getString("date");
                            String spend = obj.getString("spend");
                            String categoryName = obj.getString("title");
                            String iconId = obj.getString("iconId");
                            String cateIdStr = obj.getString("cateId");
                            UUID cateId = UUID.fromString(cateIdStr);
                            expenseList.add(new ExpenseResponse(expenseId,date, spend, categoryName, iconId,cateId));
                        }
                        successCallback.onSuccess(expenseList);
                    } catch (JSONException e) {
                        errorCallback.onError("Lỗi xử lý dữ liệu JSON");
                    }
                },
                error -> errorCallback.onError("Lỗi kết nối: " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }
    public void fetchRecentExpenses(
            String accessToken,
            UUID userId,
            SuccessListenerExpense successCallback,
            ErrorListenerExpense errorCallback
    ) {
        String url = baseUrl + "/expenses/recent/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<ExpenseResponse> expenseList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String expenseStr = obj.getString("expenseId");
                            UUID expenseId = UUID.fromString(expenseStr);
                            String date = obj.getString("date");
                            String spend = obj.getString("spend");
                            String categoryName = obj.getString("title");
                            String iconId = obj.getString("iconId");
                            String cateIdStr = obj.getString("cateId");
                            UUID cateId = UUID.fromString(cateIdStr);
                            expenseList.add(new ExpenseResponse(expenseId,date, spend, categoryName, iconId,cateId));
                        }
                        successCallback.onSuccess(expenseList);
                    } catch (JSONException e) {
                        errorCallback.onError("Lỗi xử lý dữ liệu JSON");
                    }
                },
                error -> errorCallback.onError("Lỗi kết nối: " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }
    public void fetchCategoriesByUserId(
            String accessToken,
            UUID userId,
            SuccessListener successCallback,
            ErrorListener errorCallback
    ) {
        String url = baseUrl + "/categories/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<CategoriesResponse> categories = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String cateIdStr = obj.getString("cateId");
                            UUID cateId = UUID.fromString(cateIdStr);
                            String title = obj.getString("title");
                            String iconId = obj.getString("iconId");
                            categories.add(new CategoriesResponse(cateId,title, iconId));
                        }
                        successCallback.onSuccess(categories);
                    } catch (JSONException e) {
                        errorCallback.onError("Lỗi xử lý dữ liệu JSON");
                    }
                },
                error -> errorCallback.onError("Lỗi kết nối: " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public interface SuccessListener {
        void onSuccess(List<CategoriesResponse> categories);
    }

    public interface ErrorListener {
        void onError(String errorMessage);
    }
    public interface SuccessListenerSumSpend {
        void onSuccess(BigDecimal spend);
    }

    public interface ErrorListenerSumSpend {
        void onError(String errorMessage);
    }
    public interface SuccessListenerExpense {
        void onSuccess(List<ExpenseResponse> expenses);
    }

    public interface ErrorListenerExpense {
        void onError(String errorMessage);
    }


    public void getAllCategories(String accessToken, CategoriesCallback callback) {
        String url = baseUrl + "/categories";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<CategoriesResponse> categories = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            String cateId = obj.getString("cateId");
                            String title = obj.getString("title");
                            String iconId = obj.getString("iconId");

                            CategoriesResponse category = new CategoriesResponse(UUID.fromString(cateId), title, iconId);
                            categories.add(category);
                        }

                        callback.onSuccess(categories);

                    } catch (JSONException e) {
                        callback.onError("Lỗi khi xử lý dữ liệu JSON");
                    }
                },
                error -> callback.onError("Có lỗi xảy ra")
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);
    }
    public void addExpense(String accessToken, String userId, String date, BigDecimal spend, String cateId,
                           Runnable onSuccess, Consumer<String> onError) {
        String url = baseUrl + "/expenses";
        JSONObject body = new JSONObject();
        try {
            body.put("userId", userId);
            body.put("date", date);
            body.put("spend", spend);
            body.put("cateId", cateId);
        } catch (JSONException e) {
            onError.accept("Invalid data");
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> onSuccess.run(),
                error -> {
                    String errMsg = "Unknown error";
                    if (error.networkResponse != null) {
                        errMsg = new String(error.networkResponse.data);
                    }
                    onError.accept(errMsg);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }
    public void addCategory(String token, String userId, String title, String iconId,
                            Runnable onSuccess, Consumer<String> onError) {

        String url = BASE_URL + "/categories";

        try {
            JSONObject body = new JSONObject();
            body.put("title", title);
            body.put("iconId", iconId);
            body.put("userId", userId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> onSuccess.run(),
                    error -> {
                        String errMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        onError.accept(errMsg);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            onError.accept("Lỗi JSON: " + e.getMessage());
        }
    }
    public void updateCategory(String accessToken, UUID cateId, String title, String iconId, String userId,
                               Runnable onSuccess, Consumer<String> onError) {
        String url = "http://10.0.2.2:8000/categories/" + cateId;


        try {
            JSONObject body = new JSONObject();
            body.put("cateId", cateId.toString());
            body.put("title", title);
            body.put("iconId", iconId);
            body.put("userId", userId);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, body,
                    response -> onSuccess.run(),
                    error -> onError.accept(error.toString())
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + accessToken);
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            onError.accept("Lỗi tạo JSON: " + e.getMessage());
        }
    }
    public void deleteCategory(
            String accessToken,
            UUID categoryId,
            SuccessListener successCallback,
            ErrorListener errorCallback
    ) {
        String url = baseUrl + "/categories/" + categoryId;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> successCallback.onSuccess(null),
                error -> {
                    String errorMessage = "Lỗi xoá danh mục";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    errorCallback.onError(errorMessage);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(deleteRequest);
    }
    public void updateExpense(String accessToken, UUID expenseId, String userId, String date, BigDecimal spend, String cateId,
                              Runnable onSuccess, Consumer<String> onError) {
        String url = baseUrl + "/expenses/" + expenseId;

        JSONObject body = new JSONObject();
        try {
            body.put("userId", userId);
            body.put("date", date);
            body.put("spend", spend);
            body.put("cateId", cateId);
        } catch (JSONException e) {
            onError.accept("Invalid data");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, body,
                response -> onSuccess.run(),
                error -> {
                    String errMsg = error.networkResponse != null
                            ? new String(error.networkResponse.data)
                            : "Unknown error";
                    onError.accept(errMsg);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void deleteExpense(
            String accessToken,
            UUID expenseId,
            SuccessListenerExpense successCallback,
            ErrorListenerExpense errorCallback
    ) {
        String url = baseUrl + "/expenses/" + expenseId;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> successCallback.onSuccess(null),
                error -> {
                    String errorMessage = "Lỗi xoá chi tiêu";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    errorCallback.onError(errorMessage);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(deleteRequest);
    }
    public void getMonthlyExpenseTotal(String accessToken, UUID userId,
                                       SuccessListenerSumSpend successCallback,
                                       ErrorListenerSumSpend errorCallback) {
        String url = baseUrl + "/expenses/monthly-total/" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        BigDecimal total = new BigDecimal(response.trim());
                        successCallback.onSuccess(total);
                    } catch (NumberFormatException e) {
                        errorCallback.onError("Lỗi định dạng dữ liệu từ server");
                    }
                },
                error -> {
                    String errorMessage = "Lỗi kết nối";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    errorCallback.onError(errorMessage); // BẠN ĐÃ THIẾU DÒNG NÀY
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }


}

