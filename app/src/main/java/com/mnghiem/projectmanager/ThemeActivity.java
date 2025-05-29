package com.mnghiem.projectmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.GeneralResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemeActivity extends BaseActivity {

    private ImageView btnBack;
    private CheckBox checkDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        // Gán view
        btnBack = findViewById(R.id.btnBack);
        checkDarkMode = findViewById(R.id.checkDarkMode);

        // Lấy userId và trạng thái dark mode từ local
        SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        boolean isDark = prefs.getBoolean("darkMode", false);
        checkDarkMode.setChecked(isDark);

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

    }
}
