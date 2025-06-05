package com.mnghiem.projectmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.ColorRequest;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Project;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivity extends BaseActivity {

    private LinearLayout createdContainer, joinedContainer;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        setupTopAndBottomBar();

        createdContainer = findViewById(R.id.createdContainer);
        joinedContainer = findViewById(R.id.joinedContainer);

        currentUserId = getSharedPreferences("USER_PREF", MODE_PRIVATE).getInt("userId", -1);
        Log.d("DEBUG_PROJECT", "👉 userId: " + currentUserId);

        if (currentUserId != -1) {
            fetchPersonalProjects(currentUserId);
            fetchJoinedProjects(currentUserId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPersonalProjects(int userId) {
        MyAPI myAPI = APIClient.getClient().create(MyAPI.class);
        myAPI.getPersonalProjects(userId).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Project p : response.body()) {
                        createdContainer.addView(buildProjectCard(p));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Log.e("DEBUG_PROJECT", "❌ Lỗi khi gọi API nhóm cá nhân: " + t.getMessage());
            }
        });
    }

    private void fetchJoinedProjects(int userId) {
        MyAPI myAPI = APIClient.getClient().create(MyAPI.class);
        myAPI.getJoinedProjects(userId).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Project p : response.body()) {
                        joinedContainer.addView(buildProjectCard(p));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Log.e("DEBUG_PROJECT", "❌ Lỗi khi gọi API nhóm tham gia: " + t.getMessage());
            }
        });
    }

    private View buildProjectCard(Project project) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(project.getMauNen()), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        card.setBackground(gradient);
        card.setElevation(12f);
        card.setPadding(32, 32, 32, 32);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(24, 24, 24, 24);
        card.setLayoutParams(cardParams);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText(project.getTenNhom());
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        topRow.addView(title);

        ImageView btnMore = new ImageView(this);
        btnMore.setImageResource(R.drawable.ic_more_vert);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(64, 64);
        btnMore.setLayoutParams(iconParams);
        btnMore.setOnClickListener(v -> showProjectMenu(v, project));
        topRow.addView(btnMore);

        card.addView(topRow);

        if (project.getMoTa() != null && !project.getMoTa().isEmpty()) {
            TextView desc = new TextView(this);
            desc.setText(project.getMoTa());
            desc.setTextSize(13);
            desc.setTextColor(Color.DKGRAY);
            desc.setPadding(0, 8, 0, 8);
            card.addView(desc);
        }

        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#CCCCCC"));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2);
        dividerParams.setMargins(0, 24, 0, 16);
        divider.setLayoutParams(dividerParams);
        card.addView(divider);

        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setWeightSum(2);
        bottomRow.setPadding(0, 12, 0, 12);

        TextView boardStat = new TextView(this);
        boardStat.setText(project.getSoBoard() + " boards");
        boardStat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        boardStat.setTextSize(14);
        boardStat.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        bottomRow.addView(boardStat);

        TextView taskStat = new TextView(this);
        taskStat.setText("0 tasks");
        taskStat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        taskStat.setTextSize(14);
        taskStat.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        bottomRow.addView(taskStat);

        card.addView(bottomRow);

        card.setOnClickListener(v -> openBoardDetail(project.getTenNhom(), project.getMaNhom()));

        return card;
    }

    private void openBoardDetail(String boardTitle, int maNhom) {
        Intent intent = new Intent(this, ProjectDetailActivity.class);
        intent.putExtra("boardTitle", boardTitle);
        intent.putExtra("ma_nhom", maNhom);
        intent.putExtra("currentUserId", currentUserId);
        startActivity(intent);
    }

    private void showProjectMenu(View anchor, Project project) {
        PopupMenu popup = new PopupMenu(this, anchor);
        boolean isOwner = (currentUserId == project.getMaNguoiTao());

        popup.getMenu().add("Cập nhật");
        popup.getMenu().add("Đổi màu");

        if (isOwner) {
            popup.getMenu().add("Mời thành viên");
            popup.getMenu().add("Xoá");
        }

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Cập nhật":
                    showEditWorkspaceDialog(project);
                    break;
                case "Đổi màu":
                    showColorPicker(project.getMaNhom());
                    break;
                case "Mời thành viên":
                    //new InviteMemberDialog(this, project.getMaNhom(), currentUserId).show();
                    break;
                case "Xoá":
                    confirmDeleteWorkspace(project.getMaNhom());
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void showEditWorkspaceDialog(Project project) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_board, null);
        EditText edtTitle = dialogView.findViewById(R.id.edtBoardTitle);
        EditText edtDesc = dialogView.findViewById(R.id.edtBoardDescription);

        edtTitle.setText(project.getTenNhom());
        edtDesc.setText(project.getMoTa());

        new android.app.AlertDialog.Builder(this)
                .setTitle("Sửa workspace")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newTitle = edtTitle.getText().toString().trim();
                    String newDesc = edtDesc.getText().toString().trim();

                    Project updated = new Project(newTitle, newDesc);
                    MyAPI api = APIClient.getClient().create(MyAPI.class);
                    api.updateGroup(project.getMaNhom(), updated).enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                            if (response.isSuccessful()) recreate();
                            else Toast.makeText(ProjectActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {
                            Toast.makeText(ProjectActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void confirmDeleteWorkspace(int maNhom) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xoá nhóm")
                .setMessage("Bạn có chắc chắn muốn xoá nhóm này không?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteWorkspace(maNhom))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteWorkspace(int maNhom) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.deleteGroup(maNhom).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProjectActivity.this, "Đã xoá nhóm", Toast.LENGTH_SHORT).show();
                    recreate();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(ProjectActivity.this, "Lỗi xoá nhóm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showColorPicker(int maNhom) {
        final String[] colors = {"#FFF8E1", "#FFCDD2", "#C8E6C9", "#BBDEFB", "#E1BEE7"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn màu")
                .setItems(colors, (dialog, which) -> updateGroupColor(maNhom, colors[which]))
                .show();
    }

    private void updateGroupColor(int maNhom, String newColor) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.updateGroupColor(maNhom, new ColorRequest(newColor)).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
                    Toast.makeText(ProjectActivity.this, "Đã đổi màu!", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(ProjectActivity.this, "Lỗi đổi màu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(ProjectActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
