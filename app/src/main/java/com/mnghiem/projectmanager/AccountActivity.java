package com.mnghiem.projectmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    private static final String TAG = "ACCOUNT_DEBUG";
    private static final int REQUEST_CODE_IMAGE_PICK = 1001;
    private static final int REQUEST_PERMISSION = 100;

    private ImageView btnBack, imgAvatar, iconEditAvatar, iconEditName, btnEditSave;
    private TextView tvEmail, tvName, tvUserId, tvGender;
    private EditText edtName;
    private Spinner spnGender;
    private TextView btnChangePassword, btnDeleteAccount;

    private int userId;
    private String avatarUrl;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setupTopAndBottomBar();

        // Yêu cầu quyền
        checkAndRequestPermission();

        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        iconEditAvatar = findViewById(R.id.iconEditAvatar);
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvUserId = findViewById(R.id.tvUserId);
        tvGender = findViewById(R.id.tvGender);
        edtName = findViewById(R.id.edtName);
        spnGender = findViewById(R.id.spnGender);
        iconEditName = findViewById(R.id.iconEditName);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnEditSave = findViewById(R.id.btnEditSave);

        userId = getSharedPreferences("USER_PREF", MODE_PRIVATE).getInt("userId", -1);
        Log.d(TAG, "✅ userId = " + userId);
        if (userId != -1) fetchUserInfoFromServer(userId);

        btnBack.setOnClickListener(v -> finish());

        iconEditName.setOnClickListener(v -> {
            tvName.setVisibility(View.GONE);
            edtName.setVisibility(View.VISIBLE);
            edtName.requestFocus();
        });

        iconEditAvatar.setOnClickListener(v -> openGallery());

        btnChangePassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));

        btnDeleteAccount.setOnClickListener(v -> Toast.makeText(this, "Xoá tài khoản (chưa xử lý)", Toast.LENGTH_SHORT).show());

        btnEditSave.setOnClickListener(v -> {
            isEditMode = !isEditMode;
            if (isEditMode) {
                edtName.setVisibility(View.VISIBLE);
                tvName.setVisibility(View.GONE);
                iconEditAvatar.setVisibility(View.VISIBLE);
                spnGender.setVisibility(View.VISIBLE);
                tvGender.setVisibility(View.GONE);
                btnEditSave.setImageResource(R.drawable.ic_save);
            } else {
                String newName = edtName.getText().toString().trim();
                String newGender = spnGender.getSelectedItem().toString().trim();

                tvName.setText(newName);
                tvGender.setText(newGender);
                edtName.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
                iconEditAvatar.setVisibility(View.GONE);
                spnGender.setVisibility(View.GONE);
                tvGender.setVisibility(View.VISIBLE);
                btnEditSave.setImageResource(R.drawable.ic_edit);

                User updatedUser = new User();
                updatedUser.setHoTen(newName);
                updatedUser.setGioiTinh(newGender);

                Log.d(TAG, "📤 Gửi API cập nhật user: name = " + newName + ", gender = " + newGender);

                MyAPI api = APIClient.getClient().create(MyAPI.class);
                api.updateUserInfo(userId, updatedUser).enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        Log.d(TAG, "📬 Response cập nhật: " + response.code());
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Log.d(TAG, "✅ Cập nhật user thành công");
                            Toast.makeText(AccountActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "❌ Cập nhật thất bại: " + response.message());
                            Toast.makeText(AccountActivity.this, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Log.e(TAG, "❌ Lỗi kết nối khi cập nhật user", t);
                        Toast.makeText(AccountActivity.this, "Lỗi kết nối khi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "✅ Quyền truy cập ảnh đã được cấp");
            } else {
                Log.e(TAG, "❌ Người dùng từ chối quyền truy cập ảnh");
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchUserInfoFromServer(int userId) {
        Log.d(TAG, "📥 Gọi API lấy user với ID: " + userId);
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "📬 Response lấy user: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d(TAG, "✅ User data: " + user.getHoTen() + ", " + user.getEmail());

                    tvUserId.setText("ID: " + user.getMaNd());
                    tvEmail.setText(user.getEmail());

                    String name = user.getHoTen() != null ? user.getHoTen() : "";
                    tvName.setText(name);
                    edtName.setText(name);

                    String gender = user.getGioiTinh() != null ? user.getGioiTinh() : "";
                    tvGender.setText(gender);

                    String[] genderArray = getResources().getStringArray(R.array.gender_array);
                    for (int i = 0; i < genderArray.length; i++) {
                        if (gender.equalsIgnoreCase(genderArray[i])) {
                            spnGender.setSelection(i);
                            break;
                        }
                    }

                    avatarUrl = user.getAnhDaiDien();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(AccountActivity.this).load(avatarUrl).circleCrop().into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                    }
                } else {
                    Log.e(TAG, "❌ Không lấy được thông tin user");
                    Toast.makeText(AccountActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi kết nối khi gọi API getUserById", t);
                Toast.makeText(AccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Log.d(TAG, "📸 Mở gallery chọn ảnh");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Log.d(TAG, "🖼 URI ảnh chọn: " + imageUri);
            if (imageUri != null) {
                uploadAvatar(imageUri);
            }
        }
    }

    private void uploadAvatar(Uri imageUri) {
        try {
            Log.d(TAG, "🔁 Bắt đầu upload avatar...");
            File file = new File(PathUtil.getPath(this, imageUri));
            Log.d(TAG, "📁 File path: " + file.getAbsolutePath());

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

            MyAPI api = APIClient.getClient().create(MyAPI.class);
            api.uploadAvatar(body, userIdPart).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    Log.d(TAG, "📬 Response upload avatar: " + response.code());
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        avatarUrl = response.body().getMessage();
                        Log.d(TAG, "✅ Upload thành công, URL: " + avatarUrl);

                        Glide.with(AccountActivity.this).load(avatarUrl).circleCrop().into(imgAvatar);

                        SharedPreferences.Editor editor = getSharedPreferences("USER_PREF", MODE_PRIVATE).edit();
                        editor.putString("avatarUrl", avatarUrl);
                        editor.apply();

                        Toast.makeText(AccountActivity.this, "Cập nhật ảnh thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "❌ Upload thất bại: " + response.message());
                        Toast.makeText(AccountActivity.this, "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Log.e(TAG, "❌ Lỗi kết nối khi upload avatar", t);
                    Toast.makeText(AccountActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Exception khi đọc đường dẫn ảnh", e);
            Toast.makeText(this, "Không thể đọc đường dẫn ảnh", Toast.LENGTH_SHORT).show();
        }
    }
}
