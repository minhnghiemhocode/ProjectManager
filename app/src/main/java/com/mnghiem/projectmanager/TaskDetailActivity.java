package com.mnghiem.projectmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Attachment;
import com.mnghiem.projectmanager.models.Checklist;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends AppCompatActivity {

    private int taskId, currentUserId;
    private TextView tvTitle, tvDescription, tvStatus, tvPriority, tvDeadline, tvAssignee;
    private LinearLayout checklistContainer, attachmentContainer;
    private ImageView btnBack, btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskId = getIntent().getIntExtra("task_id", -1);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        tvTitle = findViewById(R.id.tvTaskTitle);
        tvDescription = findViewById(R.id.tvTaskDescription);
        tvStatus = findViewById(R.id.tvTaskStatus);
        tvPriority = findViewById(R.id.tvTaskPriority);
        tvDeadline = findViewById(R.id.tvTaskDeadline);
        tvAssignee = findViewById(R.id.tvTaskAssignee);
        checklistContainer = findViewById(R.id.checklistContainer);
        attachmentContainer = findViewById(R.id.attachmentContainer);
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnTaskMenu);

        btnBack.setOnClickListener(v -> finish());
        btnMenu.setOnClickListener(this::showPopupMenu);

        if (taskId != -1) {
            loadTaskDetail(taskId);
            loadAttachments(taskId);
        }
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 1, "Edit task");
        popup.getMenu().add(0, 2, 2, "Delete task");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                Intent intent = new Intent(TaskDetailActivity.this, EditTaskActivity.class);
                intent.putExtra("task_id", taskId);
                intent.putExtra("currentUserId", currentUserId);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == 2) {
                confirmDeleteTask();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void confirmDeleteTask() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm deletion")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.deleteTask(taskId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TaskDetailActivity.this, "Đã xoá task", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TaskDetailActivity.this, BoardDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(TaskDetailActivity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this, "Lỗi xoá task", Toast.LENGTH_SHORT).show();
                Log.e("TASK_DETAIL", "❌ Lỗi xoá task: " + t.getMessage());
            }
        });
    }

    private void loadTaskDetail(int taskId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getTaskById(taskId).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Task task = response.body();

                    tvTitle.setText(task.getTieu_de());
                    tvDescription.setText(task.getMo_ta() != null ? task.getMo_ta() : "Enter task description");
                    // Trạng thái
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
                        default:
                            statusLabel = task.getTrang_thai();
                    }
                    tvStatus.setText(statusLabel);

// Ưu tiên
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
                    tvPriority.setText(priorityLabel);

                    tvDeadline.setText(task.getHan_hoan_thanh());
                    tvAssignee.setText(task.getNguoiGiao() != null ? task.getNguoiGiao() : "Not assigned");

                    loadChecklist(task.getMa_cv());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("TASK_DETAIL", "❌ Lỗi kết nối khi load task: " + t.getMessage());
            }
        });
    }

    private void loadChecklist(int taskId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getChecklistByTask(taskId).enqueue(new Callback<List<Checklist>>() {
            @Override
            public void onResponse(Call<List<Checklist>> call, Response<List<Checklist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    checklistContainer.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(TaskDetailActivity.this);

                    for (Checklist item : response.body()) {
                        View row = inflater.inflate(R.layout.item_checklist_row, checklistContainer, false);
                        CheckBox cb = row.findViewById(R.id.cbChecklist);

                        cb.setText(item.getNoiDung());
                        cb.setChecked(item.isDaHoanThanh());
                        cb.setTextColor(item.isDaHoanThanh() ? 0xFF4CAF50 : 0xFF212121);

                        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            item.setDaHoanThanh(isChecked);
                            cb.setTextColor(isChecked ? 0xFF4CAF50 : 0xFF212121);

                            api.updateChecklistStatus(item.getMaItem(), item).enqueue(new Callback<GeneralResponse>() {
                                @Override
                                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {}

                                @Override
                                public void onFailure(Call<GeneralResponse> call, Throwable t) {}
                            });
                        });

                        checklistContainer.addView(row);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Checklist>> call, Throwable t) {}
        });
    }

    private void loadAttachments(int taskId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getAttachmentsByTask(taskId).enqueue(new Callback<List<Attachment>>() {
            @Override
            public void onResponse(Call<List<Attachment>> call, Response<List<Attachment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    attachmentContainer.removeAllViews();
                    for (Attachment item : response.body()) {
                        addAttachmentView(item);
                    }
                    Log.d("TASK_ATTACH", "✅ Tải " + response.body().size() + " tệp đính kèm");
                }
            }

            @Override
            public void onFailure(Call<List<Attachment>> call, Throwable t) {
                Log.e("TASK_ATTACH", "❌ Lỗi tải tệp đính kèm: " + t.getMessage());
            }
        });
    }

    private void addAttachmentView(Attachment attachment) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_attachment_row, attachmentContainer, false);
        TextView tvFileName = row.findViewById(R.id.tvFileName);
        ImageView btnOpen = row.findViewById(R.id.btnOpenFile);

        String fileName = attachment.getDuongDan().substring(attachment.getDuongDan().lastIndexOf("/") + 1);
        tvFileName.setText(fileName);

        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(attachment.getDuongDan()));
            startActivity(intent);
        });

        attachmentContainer.addView(row);
    }
}
