package com.mnghiem.projectmanager.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.mnghiem.projectmanager.AccountActivity;
import com.mnghiem.projectmanager.HelpActivity;
import com.mnghiem.projectmanager.LoginActivity;
import com.mnghiem.projectmanager.R;
import com.mnghiem.projectmanager.StatisticsActivity;
import com.mnghiem.projectmanager.ThemeActivity;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private TextView btnTheme, btnStats, btnHelp, btnLogout;
    private View btnAccount;
    private ImageView imgAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnAccount = view.findViewById(R.id.btnAccount);
        btnTheme = view.findViewById(R.id.btnTheme);
        btnStats = view.findViewById(R.id.btnStats);
        btnHelp = view.findViewById(R.id.btnHelp);
        btnLogout = view.findViewById(R.id.btnLogout);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        SharedPreferences prefs = requireActivity().getSharedPreferences("USER_PREF", requireContext().MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            fetchUserAvatar(userId);
        }

        btnAccount.setOnClickListener(v -> startActivity(new Intent(requireContext(), AccountActivity.class)));
        btnTheme.setOnClickListener(v -> startActivity(new Intent(requireContext(), ThemeActivity.class)));
        btnStats.setOnClickListener(v -> startActivity(new Intent(requireContext(), StatisticsActivity.class)));
        btnHelp.setOnClickListener(v -> startActivity(new Intent(requireContext(), HelpActivity.class)));

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void fetchUserAvatar(int userId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String avatarUrl = response.body().getAnhDaiDien();
                    Log.d("SETTINGS_DEBUG", "✅ avatarUrl từ API: " + avatarUrl);

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(requireContext())
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_avatar_placeholder)
                                .error(R.drawable.ic_avatar_placeholder)
                                .circleCrop()
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                    }
                } else {
                    Log.w("SETTINGS_DEBUG", "⚠️ Không lấy được user hoặc avatar");
                    imgAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("SETTINGS_DEBUG", "❌ Lỗi kết nối API lấy user", t);
                imgAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }
        });
    }
}
