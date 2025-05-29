package com.mnghiem.projectmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailActivity extends AppCompatActivity {

    private LinearLayout taskListContainer;
    private int boardId;
    private String boardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        taskListContainer = findViewById(R.id.taskListContainer);
        TextView tvBoardTitle = findViewById(R.id.tvBoardTitle);
        ImageView btnBoardMenu = findViewById(R.id.btnBoardMenu);
        ImageButton btnAddTask = findViewById(R.id.btnAddTask);
        ImageView btnBack = findViewById(R.id.btnBack);

        // ✅ Sửa key nhận dữ liệu đúng với intent bên ProjectDetailActivity
        boardId = getIntent().getIntExtra("board_id", -1);
        boardName = getIntent().getStringExtra("board_title");

        tvBoardTitle.setText(boardName != null ? boardName : "Board");

        // Nút back
        btnBack.setOnClickListener(v -> finish());

        btnBoardMenu.setOnClickListener(v -> showPopupMenu(btnBoardMenu));

        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            intent.putExtra("boardId", boardId);
            startActivity(intent);
        });

        loadTasks();
    }


    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Cập nhật");
        popup.getMenu().add("Xoá");
        popup.getMenu().add("Thêm Task");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals("Cập nhật")) {
                // Hiện popup cập nhật
            } else if (title.equals("Xoá")) {
                // Gọi API xoá board
            } else if (title.equals("Thêm Task")) {
                Intent intent = new Intent(this, AddTaskActivity.class);
                intent.putExtra("boardId", boardId);
                startActivity(intent);
            }
            return true;
        });
        popup.show();
    }

    private View buildTaskCard(Task task) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        card.setElevation(10f);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#E1F5FE"), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        card.setBackground(gradient);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 32);
        card.setLayoutParams(params);

        TextView title = new TextView(this);
        title.setText(task.getTitle());
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        card.addView(title);

        TextView status = new TextView(this);
        status.setText("Trạng thái: " + task.getStatus());
        status.setTextColor(Color.DKGRAY);
        status.setPadding(0, 8, 0, 0);
        card.addView(status);

        TextView priority = new TextView(this);
        priority.setText("Ưu tiên: " + task.getPriority());
        priority.setTextColor(Color.DKGRAY);
        card.addView(priority);

        TextView deadline = new TextView(this);
        deadline.setText("Hạn: " + task.getDeadline());
        deadline.setTextColor(Color.DKGRAY);
        card.addView(deadline);

        // Avatar thu nhỏ (giả sử bạn đã có URL ảnh)
        ImageView avatar = new ImageView(this);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
        avatar.setImageResource(R.drawable.ic_avatar_placeholder); // Có thể dùng Glide để load ảnh URL
        card.addView(avatar);

        return card;
    }

    private void loadTasks() {
        taskListContainer.removeAllViews();

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getTasksByBoard(boardId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Task task : response.body()) {
                        taskListContainer.addView(buildTaskCard(task));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
