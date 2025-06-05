package com.mnghiem.projectmanager;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mnghiem.projectmanager.fragments.HomeFragment;
import com.mnghiem.projectmanager.fragments.ProjectFragment;
import com.mnghiem.projectmanager.fragments.DeadlineFragment;
import com.mnghiem.projectmanager.fragments.SettingsFragment;
import com.mnghiem.projectmanager.ui.CreateWorkspaceBottomSheet;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNav;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.topAppBar);
        bottomNav = findViewById(R.id.bottomNav);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        // Hiệu ứng gradient lấp lánh cho title
        toolbarTitle.post(() -> {
            int width = toolbarTitle.getWidth();

            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration(2500);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);

            animator.addUpdateListener(animation -> {
                float progress = (float) animation.getAnimatedValue();

                Shader shader = new LinearGradient(
                        width * progress, 0, width * (progress + 0.3f), 0,
                        new int[]{
                                Color.parseColor("#42A5F5"), // xanh dương
                                Color.parseColor("#B2EBF2"), // tím
                                Color.parseColor("#42A5F5")  // xanh dương
                        },
                        null, Shader.TileMode.CLAMP
                );

                toolbarTitle.getPaint().setShader(shader);
                toolbarTitle.invalidate();
            });

            animator.start();
        });

        // Mặc định mở HomeFragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Menu trên cùng
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_account) {
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }
            return false;
        });

        // Điều hướng bottom nav
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_projects) {
                selectedFragment = new ProjectFragment();
            } else if (id == R.id.nav_deadlines) {
                selectedFragment = new DeadlineFragment();
            } else if (id == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            } else if (id == R.id.nav_add) {
                CreateWorkspaceBottomSheet bottomSheet = new CreateWorkspaceBottomSheet();
                bottomSheet.setWorkspaceCreateListener(newProject -> {
                    Intent intent = new Intent(this, ProjectDetailActivity.class);
                    intent.putExtra("workspace_id", newProject.getMaNhom());
                    intent.putExtra("workspace_name", newProject.getTenNhom());
                    startActivity(intent);
                });
                bottomSheet.show(getSupportFragmentManager(), "create_workspace");
                return false;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
