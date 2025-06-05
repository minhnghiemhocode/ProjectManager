package com.mnghiem.projectmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
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
    private int boardId, groupId, currentUserId;
    private String boardName, boardDescription;
    private boolean sortByDeadline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        boardId = getIntent().getIntExtra("board_id", -1);
        groupId = getIntent().getIntExtra("group_id", -1);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);
        boardName = getIntent().getStringExtra("board_title");
        boardDescription = getIntent().getStringExtra("board_description");

        Log.d("DEBUG_BOARD_FLOW", "🔍 Nhận dữ liệu từ intent: boardId=" + boardId
                + ", boardTitle=" + boardName + ", boardDesc=" + boardDescription
                + ", groupId=" + groupId + ", userId=" + currentUserId);

        TextView tvBoardTitle = findViewById(R.id.tvBoardTitle);
        TextView tvBoardDescription = findViewById(R.id.tvBoardDescription);
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnMenu = findViewById(R.id.btnBoardMenu);
        ImageButton btnSort = findViewById(R.id.btnSortByDate);
        taskListContainer = findViewById(R.id.taskListContainer);

        tvBoardTitle.setText(boardName);
        tvBoardDescription.setText(boardDescription != null ? boardDescription : "");

        btnBack.setOnClickListener(v -> finish());
        btnMenu.setOnClickListener(v -> showPopupMenu(btnMenu));

        btnSort.setOnClickListener(v -> {
            sortByDeadline = !sortByDeadline;
            btnSort.setImageResource(sortByDeadline ? R.drawable.ic_sort_desc : R.drawable.ic_sort_asc);
            loadTasks();
        });

        loadTasks();
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Edit folder");
        popup.getMenu().add("Delete folder");
        popup.getMenu().add("Add task");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Edit folder":
                    // TODO: show edit dialog
                    break;
                case "Delete folder":
                    // TODO: confirm and delete board
                    break;
                case "Add task":
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
        Log.d("DEBUG_TASK_LOAD", "🚀 Gọi API lấy task cho boardId=" + boardId);
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getTasksByBoard(boardId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> taskList = response.body();

                    if (sortByDeadline) {
                        taskList.sort((t1, t2) -> {
                            String d1 = t1.getHan_hoan_thanh();
                            String d2 = t2.getHan_hoan_thanh();
                            if (d1 == null && d2 == null) return 0;
                            if (d1 == null) return 1;
                            if (d2 == null) return -1;
                            return d2.compareTo(d1); // Trễ nhất lên trước
                        });
                    }

                    for (Task task : taskList) {
                        try {
                            View taskView = buildTaskCard(task);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(64, 0, 64, 40);
                            taskView.setLayoutParams(params);
                            taskListContainer.addView(taskView);
                        } catch (Exception e) {
                            Log.e("DEBUG_TASK_LOAD", "❗Lỗi khi tạo view cho task: " + task.getTieu_de(), e);
                        }
                    }
                } else {
                    Log.e("DEBUG_TASK_LOAD", "⚠️ API thành công nhưng không có task hoặc body null.");
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("DEBUG_TASK_LOAD", "❌ Lỗi kết nối khi load task", t);
            }
        });
    }

    private View buildTaskCard(Task task) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        card.setElevation(12f);

        String color = "#B2EBF2"; // mặc định vàng nhạt
        switch (task.getTrang_thai()) {
            case "hoan_thanh": color = "#5EE063"; break;
            case "dang_lam": color = "#B3E5FC"; break;
            case "cho_xu_ly": color = "#F0F095"; break;
        }

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(color), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        card.setBackground(gradient);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24, 24, 24, 24);
        card.setLayoutParams(params);

        TextView title = new TextView(this);
        title.setText(task.getTieu_de());
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        card.addView(title);

        TextView status = new TextView(this);
        String statusLabel;
        switch (task.getTrang_thai()) {
            case "hoan_thanh":
                statusLabel = "Hoàn thành";
                break;
            case "dang_lam":
                statusLabel = "Đang làm";
                break;
            case "cho_xu_ly":
                statusLabel = "Chờ xử lý";
                break;
            case "trung_binh":
                statusLabel = "Trung bình";
                break;
            default:
                statusLabel = task.getTrang_thai();
        }
        status.setText("Trạng thái: " + statusLabel);

        status.setTextColor(Color.DKGRAY);
        status.setPadding(0, 8, 0, 8);
        card.addView(status);

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

        TextView deadline = new TextView(this);
        deadline.setText("Duo date: " + (task.getHan_hoan_thanh() != null ? task.getHan_hoan_thanh() : "—"));
        deadline.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        deadline.setTextSize(14);
        deadline.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        bottomRow.addView(deadline);

        TextView priority = new TextView(this);
        String priorityLabel;
        switch (task.getDo_uu_tien()) {
            case "thap":
                priorityLabel = "Thấp";
                break;
            case "trung_binh":
                priorityLabel = "Trung bình";
                break;
            case "cao":
                priorityLabel = "Cao";
                break;
            default:
                priorityLabel = task.getDo_uu_tien();
        }
        priority.setText("Ưu tiên: " + priorityLabel);
        priority.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        priority.setTextSize(14);
        priority.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        bottomRow.addView(priority);

        card.addView(bottomRow);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getMa_cv());
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        return card;
    }
}
