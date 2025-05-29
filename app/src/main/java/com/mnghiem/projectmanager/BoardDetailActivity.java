package com.mnghiem.projectmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailActivity extends AppCompatActivity {

    private LinearLayout taskListContainer;
    private int boardId, groupId, currentUserId;
    private String boardName, boardDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        boardId = getIntent().getIntExtra("board_id", -1);
        groupId = getIntent().getIntExtra("group_id", -1);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);
        boardName = getIntent().getStringExtra("board_title");
        boardDescription = getIntent().getStringExtra("board_description");

        TextView tvBoardTitle = findViewById(R.id.tvBoardTitle);
        TextView tvBoardDescription = findViewById(R.id.tvBoardDescription);
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnMenu = findViewById(R.id.btnBoardMenu);
        taskListContainer = findViewById(R.id.taskListContainer);

        tvBoardTitle.setText(boardName);
        tvBoardDescription.setText(boardDescription != null ? boardDescription : "");

        btnBack.setOnClickListener(v -> finish());
        btnMenu.setOnClickListener(v -> showPopupMenu(btnMenu));

        loadTasks();
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Sửa");
        popup.getMenu().add("Xoá");
        popup.getMenu().add("Thêm Task");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Sửa":
                    // TODO: show edit dialog
                    break;
                case "Xoá":
                    // TODO: confirm and delete board
                    break;
                case "Thêm Task":
                    Intent intent = new Intent(this, AddTaskActivity.class);
                    intent.putExtra("boardId", boardId);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("currentUserId", currentUserId);
                    startActivity(intent);
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void loadTasks() {
        taskListContainer.removeAllViews();

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getTasksByBoard(boardId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Task task : response.body()) {
                        View taskView = buildTaskCard(task);

                        // Đặt khoảng cách dưới mỗi task là 40dp
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 0, 0, 40);
                        taskView.setLayoutParams(params);

                        taskListContainer.addView(taskView);
                    }
                } else {
                    Log.e("BoardDetail", "Lỗi khi lấy task: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("BoardDetail", "Lỗi kết nối", t);
            }
        });
    }

    private View buildTaskCard(Task task) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_task_card, null);

        TextView tvTitle = view.findViewById(R.id.tvTaskTitle);
        TextView tvStatus = view.findViewById(R.id.tvTaskStatus);
        TextView tvDeadline = view.findViewById(R.id.tvTaskDeadline);
        View priorityDot = view.findViewById(R.id.priorityDot);
        ImageView imgAssignee = view.findViewById(R.id.imgAssignee);

        tvTitle.setText(task.getTieu_de());
        tvStatus.setText(task.getTrang_thai());
        tvDeadline.setText("Hạn: " + task.getHan_hoan_thanh());

        // Màu dot theo độ ưu tiên
        switch (task.getDo_uu_tien()) {
            case "cao":
                priorityDot.setBackgroundColor(Color.RED);
                break;
            case "trung_binh":
                priorityDot.setBackgroundColor(Color.parseColor("#FFA500"));
                break;
            default:
                priorityDot.setBackgroundColor(Color.YELLOW);
                break;
        }

        // Avatar người được giao (nếu có), bo tròn
        Glide.with(this)
                .load(task.getAssigneeAvatarUrl())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(imgAssignee);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getMa_cv());
            intent.putExtra("currentUserId", currentUserId);
            Log.d("BoardDetail", "🟢 Open TaskDetailActivity with taskId = " + task.getMa_cv());
            startActivity(intent);
        });

        return view;
    }
}
