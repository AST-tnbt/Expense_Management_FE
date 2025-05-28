package com.example.expense_management.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.expense_management.R;
import com.example.expense_management.adapters.CategoryAdapter;
import com.example.expense_management.api.ApiService;
import com.example.expense_management.dtos.CategoriesResponse;
import com.example.expense_management.models.Category;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TitleDetail extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private String base_url;
    private MaterialButton addCategoryButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_detail);

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        addCategoryButton = findViewById(R.id.addCategoryButton); // <-- Thêm dòng này để ánh xạ nút

        addCategoryButton.setOnClickListener(v -> {
            // Xử lý khi người dùng nhấn nút thêm danh mục
            Intent intent = new Intent(TitleDetail.this, AddTitle.class); // Thay bằng tên activity bạn tạo
            startActivity(intent);
        });
        categoryAdapter = new CategoryAdapter(this, categoryList, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Category category = categoryList.get(position);
                Log.d("DEBUG", "cateId: " + category.getCateId());
                Intent intent = new Intent(TitleDetail.this, EditCategoryActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                Category category = categoryList.get(position);
                UUID categoryId = category.getCateId();

                if (categoryId == null) {
                    Toast.makeText(TitleDetail.this, "Thiếu thông tin khi xoá", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show confirmation dialog
                new androidx.appcompat.app.AlertDialog.Builder(TitleDetail.this)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc chắn muốn xoá danh mục này không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        // Proceed with deletion
                        SharedPreferences prefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
                        String accessToken = prefs.getString("access_token", null);

                        ApiService apiService = new ApiService(TitleDetail.this, Volley.newRequestQueue(TitleDetail.this));
                        apiService.deleteCategory(
                                accessToken,
                                categoryId,
                                result -> {
                                    Toast.makeText(TitleDetail.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                                    categoryList.remove(position);
                                    categoryAdapter.notifyItemRemoved(position);
                                },
                                errorMsg -> {
                                    Log.e("DELETE_ERROR", errorMsg);
                                    Toast.makeText(TitleDetail.this, "Xoá thất bại: " + errorMsg, Toast.LENGTH_SHORT).show();
                                }
                        );
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);

        loadCategoriesFromApi();
    }

    private void loadCategoriesFromApi() {
        SharedPreferences userPrefs = getSharedPreferences("UserStore", Context.MODE_PRIVATE);
        String userIdStr = userPrefs.getString("id", null);
        SharedPreferences tokenPrefs = getSharedPreferences("TokenStore", Context.MODE_PRIVATE);
        String accessToken = tokenPrefs.getString("access_token", null);

        if (userIdStr == null || accessToken == null) {
            Toast.makeText(this, "Thiếu thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        UUID userId = UUID.fromString(userIdStr);

        ApiService apiService = new ApiService(this, Volley.newRequestQueue(this));
        apiService.fetchCategoriesByUserId(
                accessToken,
                userId,
                categories -> {
                    categoryList.clear();
                    for (CategoriesResponse res : categories) {
                        int iconResId = getResources().getIdentifier(res.getIconId(), "drawable", getPackageName());
                        if (iconResId == 0) iconResId = R.drawable.cake_24px;
                        categoryList.add(new Category(res.getCateId(), iconResId, res.getTitle()));
                    }
                    runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
                },
                error -> {
                    Log.e("CategoryLoadError", error);
                    Toast.makeText(TitleDetail.this, "Lỗi khi tải danh sách danh mục", Toast.LENGTH_SHORT).show();
                }
        );
    }


}
