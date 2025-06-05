package com.mnghiem.projectmanager;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mnghiem.projectmanager.ui.CreateWorkspaceBottomSheet;

public class BaseActivity extends AppCompatActivity {

    protected void setupTopAndBottomBar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);



        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_projects) {
                    startActivity(new Intent(this, ProjectActivity.class));
                    return true;
                } else if (id == R.id.nav_add) {
                    CreateWorkspaceBottomSheet bottomSheet = new CreateWorkspaceBottomSheet();

                    // Listener gọi khi tạo workspace xong
                    bottomSheet.setWorkspaceCreateListener(newProject -> {
                        // Lưu dữ liệu workspace tạm để dùng sau khi dialog đóng
                        final int groupId = newProject.getMaNhom();
                        final String title = newProject.getTenNhom();

                        // Set dismiss callback sau khi hiển thị
                        bottomSheet.show(getSupportFragmentManager(), "create_workspace");

                        // Đảm bảo Intent chỉ gọi sau khi dialog đóng
                        getSupportFragmentManager().executePendingTransactions();
                        bottomSheet.getDialog().setOnDismissListener(dialog -> {
                            Intent intent = new Intent(BaseActivity.this, ProjectDetailActivity.class);
                            intent.putExtra("ma_nhom", groupId);
                            intent.putExtra("boardTitle", title);
                            startActivity(intent);
                        });
                    });

                    return true;
                } else if (id == R.id.nav_deadlines) {
                    startActivity(new Intent(this, DeadlineActivity.class));
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
                }

                return false;
            });
        }
    }
}
