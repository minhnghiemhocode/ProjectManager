package com.mnghiem.projectmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.User;
import com.mnghiem.projectmanager.utils.PathUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends BaseActivity {

    private static final int REQUEST_CODE_IMAGE_PICK = 1001;

    private ImageView btnBack, imgAvatar, iconEditAvatar, iconEditName;
    private TextView tvTitle, tvEmail, tvName;
    private EditText edtName;
    private TextView btnChangePassword, btnDeleteAccount;

    private int userId;
    private String avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        iconEditAvatar = findViewById(R.id.iconEditAvatar);
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        edtName = findViewById(R.id.edtName);
        iconEditName = findViewById(R.id.iconEditName);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        userId = getSharedPreferences("USER_PREF", MODE_PRIVATE).getInt("userId", -1);

        if (userId != -1) {
            fetchUserInfoFromServer(userId);
        }

        btnBack.setOnClickListener(v -> finish());

        iconEditName.setOnClickListener(v -> {
            tvName.setVisibility(View.GONE);
            edtName.setVisibility(View.VISIBLE);
            edtName.requestFocus();
        });

        iconEditAvatar.setOnClickListener(v -> openGallery());

        btnChangePassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));

        btnDeleteAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Xoá tài khoản (chưa xử lý)", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchUserInfoFromServer(int userId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvEmail.setText(user.getEmail());
                    tvName.setText(user.getHoTen());
                    edtName.setText(user.getHoTen());
                    avatarUrl = user.getAnhDaiDien();

                    Glide.with(AccountActivity.this)
                            .load(avatarUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar_placeholder)
                            .into(imgAvatar);
                } else {
                    Toast.makeText(AccountActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadAvatar(imageUri);
            }
        }
    }

    private void uploadAvatar(Uri imageUri) {
        try {
            File file = new File(PathUtil.getPath(this, imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

            MyAPI api = APIClient.getClient().create(MyAPI.class);
            api.uploadAvatar(body, userIdPart).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        avatarUrl = response.body().getMessage();
                        Glide.with(AccountActivity.this)
                                .load(avatarUrl)
                                .circleCrop()
                                .into(imgAvatar);
                        Toast.makeText(AccountActivity.this, "Cập nhật ảnh thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AccountActivity.this, "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(AccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể đọc đường dẫn ảnh", Toast.LENGTH_SHORT).show();
        }
    }
}
