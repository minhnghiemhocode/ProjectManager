package com.mnghiem.projectmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Attachment;
import com.mnghiem.projectmanager.models.Checklist;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Task;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends AppCompatActivity {

    private int taskId, currentUserId;
    private Task currentTask;
    private boolean isEditing = false;

    private TextView tvTitle, tvDesc, tvStatus, tvPriority, tvDeadline, tvAssignee, pickDeadline, pickAssignee, btnSave;
    private EditText etDesc;
    private Spinner spinnerStatus, spinnerPriority;

    private LinearLayout checklistContainer, attachmentContainer;
    private TextView tvChecklistTitle, btnAddChecklist;
    private View btnAddAttachment;

    private static final int REQUEST_CODE_PICK_FILE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskId = getIntent().getIntExtra("task_id", -1);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        initViews();
        loadTask();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTaskTitle);
        tvDesc = findViewById(R.id.tvTaskDescription);
        etDesc = findViewById(R.id.etTaskDescription);
        tvStatus = findViewById(R.id.tvTaskStatus);
        spinnerStatus = findViewById(R.id.spinnerTaskStatus);
        tvPriority = findViewById(R.id.tvTaskPriority);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        tvDeadline = findViewById(R.id.tvTaskDeadline);
        pickDeadline = findViewById(R.id.pickTaskDeadline);
        tvAssignee = findViewById(R.id.tvTaskAssignee);
        pickAssignee = findViewById(R.id.pickAssignee);
        btnSave = findViewById(R.id.btnSaveChanges);

        checklistContainer = findViewById(R.id.checklistContainer);
        tvChecklistTitle = findViewById(R.id.tvChecklistTitle);
        btnAddChecklist = findViewById(R.id.btnAddChecklist);
        attachmentContainer = findViewById(R.id.attachmentContainer);
        btnAddAttachment = findViewById(R.id.btnAddAttachment);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnTaskMenu).setOnClickListener(this::showPopupMenu);

        pickDeadline.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveChanges());
        btnAddChecklist.setOnClickListener(v -> showAddChecklistDialog());
        btnAddAttachment.setOnClickListener(v -> pickFileToUpload());

        spinnerStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"hoàn thành", "chưa hoàn thành"}));

        spinnerPriority.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"cao", "trung_binh", "thap"}));
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Sửa");
        popup.getMenu().add("Xoá task");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Sửa")) {
                toggleEdit(true);
            } else if (item.getTitle().equals("Xoá task")) {
                confirmDeleteTask();
            }
            return true;
        });
        popup.show();
    }

    private void confirmDeleteTask() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá task")
                .setMessage("Bạn có chắc muốn xoá task này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    MyAPI api = APIClient.getClient().create(MyAPI.class);
                    api.deleteTask(taskId).enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                            finish();
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {
                            Toast.makeText(TaskDetailActivity.this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadTask() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getTaskById(taskId).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTask = response.body();
                    bindData();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("TaskDetail", "Lỗi khi load task", t);
            }
        });
    }

    private void bindData() {
        tvTitle.setText(currentTask.getTieu_de());
        tvDesc.setText(currentTask.getMo_ta());
        etDesc.setText(currentTask.getMo_ta());
        tvStatus.setText("Trạng thái: " + currentTask.getTrang_thai());
        tvPriority.setText("Ưu tiên: " + currentTask.getDo_uu_tien());
        tvDeadline.setText("Hạn: " + currentTask.getHan_hoan_thanh());
        pickDeadline.setText(currentTask.getHan_hoan_thanh());
        tvAssignee.setText("Người giao: (nếu có)");

        loadChecklist();
        loadAttachments();
    }

    private void toggleEdit(boolean enable) {
        isEditing = enable;
        etDesc.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvDesc.setVisibility(enable ? View.GONE : View.VISIBLE);
        spinnerStatus.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvStatus.setVisibility(enable ? View.GONE : View.VISIBLE);
        spinnerPriority.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvPriority.setVisibility(enable ? View.GONE : View.VISIBLE);
        pickDeadline.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvDeadline.setVisibility(enable ? View.GONE : View.VISIBLE);
        pickAssignee.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvAssignee.setVisibility(enable ? View.GONE : View.VISIBLE);
        btnSave.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnAddChecklist.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnAddAttachment.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void saveChanges() {
        currentTask.setMo_ta(etDesc.getText().toString());
        currentTask.setTrang_thai(spinnerStatus.getSelectedItem().toString());
        currentTask.setDo_uu_tien(spinnerPriority.getSelectedItem().toString());
        currentTask.setHan_hoan_thanh(pickDeadline.getText().toString());

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.updateTask(taskId, currentTask).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                toggleEdit(false);
                loadTask();
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.e("TaskDetail", "Lỗi khi lưu", t);
            }
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    pickDeadline.setText(dateStr);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showAddChecklistDialog() {
        EditText input = new EditText(this);
        input.setHint("Tên mục mới");
        new AlertDialog.Builder(this)
                .setTitle("Thêm mục checklist")
                .setView(input)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String content = input.getText().toString().trim();
                    if (!content.isEmpty()) {
                        Checklist item = new Checklist(taskId, content);
                        MyAPI api = APIClient.getClient().create(MyAPI.class);
                        api.addChecklistItem(item).enqueue(new Callback<GeneralResponse>() {
                            @Override
                            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                                loadChecklist();
                            }

                            @Override
                            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                                Log.e("Checklist", "Lỗi thêm", t);
                            }
                        });
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void loadChecklist() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getChecklistByTask(taskId).enqueue(new Callback<List<Checklist>>() {
            @Override
            public void onResponse(Call<List<Checklist>> call, Response<List<Checklist>> response) {
                checklistContainer.removeAllViews();
                if (response.isSuccessful() && response.body() != null) {
                    for (Checklist item : response.body()) {
                        View row = getLayoutInflater().inflate(R.layout.item_checklist_row, checklistContainer, false);
                        CheckBox cb = row.findViewById(R.id.cbChecklist);
                        ImageView btnDelete = row.findViewById(R.id.btnDeleteChecklist);

                        cb.setText(item.getNoiDung());
                        cb.setChecked(item.isDaHoanThanh());

                        cb.setOnClickListener(v -> {
                            item.setDaHoanThanh(cb.isChecked());
                            api.updateChecklistStatus(item.getMa_item(), item).enqueue(new Callback<GeneralResponse>() {
                                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {}
                                public void onFailure(Call<GeneralResponse> call, Throwable t) {}
                            });
                        });

                        cb.setOnLongClickListener(v -> {
                            EditText input = new EditText(TaskDetailActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setText(item.getNoiDung());
                            new AlertDialog.Builder(TaskDetailActivity.this)
                                    .setTitle("Sửa nội dung")
                                    .setView(input)
                                    .setPositiveButton("Lưu", (dialog, which) -> {
                                        item.setNoiDung(input.getText().toString().trim());
                                        api.updateChecklistStatus(item.getMa_item(), item).enqueue(new Callback<GeneralResponse>() {
                                            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                                                loadChecklist();
                                            }
                                            public void onFailure(Call<GeneralResponse> call, Throwable t) {}
                                        });
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                            return true;
                        });

                        btnDelete.setOnClickListener(v -> {
                            api.deleteChecklistItem(item.getMa_item()).enqueue(new Callback<GeneralResponse>() {
                                @Override
                                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                                    loadChecklist();
                                }

                                @Override
                                public void onFailure(Call<GeneralResponse> call, Throwable t) {}
                            });
                        });

                        checklistContainer.addView(row);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Checklist>> call, Throwable t) {
                Log.e("Checklist", "Lỗi load checklist", t);
            }
        });
    }

    private void loadAttachments() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getAttachmentsByTask(taskId).enqueue(new Callback<List<Attachment>>() {
            @Override
            public void onResponse(Call<List<Attachment>> call, Response<List<Attachment>> response) {
                attachmentContainer.removeAllViews();
                if (response.isSuccessful() && response.body() != null) {
                    for (Attachment a : response.body()) {
                        TextView tv = new TextView(TaskDetailActivity.this);
                        tv.setText(a.getDuongDan());
                        tv.setTextColor(0xFF1976D2);
                        attachmentContainer.addView(tv);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Attachment>> call, Throwable t) {
                Log.e("Attach", "Lỗi load attachment", t);
            }
        });
    }

    private void pickFileToUpload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            uploadFileToCloudinary(fileUri);
        }
    }

    private void uploadFileToCloudinary(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            RequestBody reqFile = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "upload", reqFile);
            RequestBody uploadPreset = RequestBody.create(MediaType.parse("multipart/form-data"), "preset");

            // API gửi file → tuỳ thuộc cấu hình
        } catch (Exception e) {
            Log.e("Upload", "Lỗi chọn file", e);
        }
    }
}
