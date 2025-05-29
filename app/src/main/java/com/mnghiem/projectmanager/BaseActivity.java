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

        if (topAppBar != null) {
            topAppBar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_search) {
                    //startActivity(new Intent(this, SearchActivity.class));
                    return true;
                } else if (id == R.id.action_notifications) {
                    startActivity(new Intent(this, NotificationActivity.class));
                    return true;
                }
                return false;
            });
        }

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
                    bottomSheet.setWorkspaceCreateListener(newProject -> {
                        Intent intent = new Intent(this, ProjectDetailActivity.class);
                        intent.putExtra("workspace_id", newProject.getMaNhom());
                        intent.putExtra("workspace_name", newProject.getTenNhom());
                        startActivity(intent);
                    });
                    bottomSheet.show(getSupportFragmentManager(), "create_workspace");
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
