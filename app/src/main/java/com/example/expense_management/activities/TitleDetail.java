package com.example.expense_management.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.adapters.CategoryAdapter;
import com.example.expense_management.api.ApiService;
import com.example.expense_management.models.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TitleDetail extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_detail);

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Category category = categoryList.get(position);
                Intent intent = new Intent(TitleDetail.this, EditCategoryActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                    Category category = categoryList.get(position);
                    UUID categoryId = category.getCateId(); // Giả sử bạn có id trong model Category

                    String url = "http://10.0.2.2:8000/categories" + categoryId; // API xóa theo ID

                    // Khởi tạo RequestQueue
                    RequestQueue queue = Volley.newRequestQueue(TitleDetail.this);

                    // Tạo request DELETE
                    StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                            response -> {
                                Toast.makeText(TitleDetail.this, "Xóa thành công", Toast.LENGTH_SHORT).show();

                                // Xóa khỏi list và cập nhật UI
                                categoryList.remove(position);
                                categoryAdapter.notifyItemRemoved(position);
                            },
                            error -> {
                                Toast.makeText(TitleDetail.this, "Xóa thất bại: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            // Nếu API cần token thì thêm header ở đây
                            SharedPreferences tokenStore = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
                            String token = tokenStore.getString("token", "");
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };

                    // Thêm request vào queue
                    queue.add(deleteRequest);
                }


        });
        categoryRecyclerView.setAdapter(categoryAdapter);

        loadCategoriesFromApi();
    }

    private void loadCategoriesFromApi() {
        SharedPreferences tokenStore = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String userIdStr = tokenStore.getString("id", null);

        if (userIdStr == null) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            return;
        }

        UUID userId = UUID.fromString(userIdStr);

        ApiService.fetchCategoriesByUserId(this, userId, new ApiService.CategoryCallback() {
            @Override
            public void onSuccess(JSONArray categoryArray) {
                categoryList.clear();
                for (int i = 0; i < categoryArray.length(); i++) {
                    try {
                        JSONObject obj = categoryArray.getJSONObject(i);
                        String title = obj.getString("title");
                        String iconIdStr = obj.getString("iconId");

                        int iconResId = getResources().getIdentifier(iconIdStr, "drawable", getPackageName());
                        if (iconResId == 0) {
                            iconResId = R.drawable.cake_24px; // icon mặc định nếu không tìm thấy
                        }

                        categoryList.add(new Category(iconResId, title));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(TitleDetail.this, "Lỗi khi tải danh sách danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
