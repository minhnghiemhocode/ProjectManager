package com.mnghiem.projectmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.bumptech.glide.Glide;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SETTINGS_DEBUG";

    private TextView btnTheme, btnStats, btnHelp, btnLogout;
    private View btnAccount;
    private ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupTopAndBottomBar();

        btnAccount = findViewById(R.id.btnAccount);
        btnTheme = findViewById(R.id.btnTheme);
        btnStats = findViewById(R.id.btnStats);
        btnHelp = findViewById(R.id.btnHelp);
        btnLogout = findViewById(R.id.btnLogout);
        imgAvatar = findViewById(R.id.imgAvatar);

        loadUserAvatar();

        btnAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
        btnTheme.setOnClickListener(v -> startActivity(new Intent(this, ThemeActivity.class)));
        btnStats.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        btnHelp.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));

        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserAvatar() {
        SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        String avatarUrl = prefs.getString("avatarUrl", null);
        Log.d(TAG, "🖼️ Avatar URL: " + avatarUrl);

        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        }
    }
}
