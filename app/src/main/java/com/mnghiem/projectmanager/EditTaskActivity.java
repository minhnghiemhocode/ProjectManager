package com.mnghiem.projectmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Attachment;
import com.mnghiem.projectmanager.models.Checklist;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Task;
import com.mnghiem.projectmanager.helpers.UploadHelper;


import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTaskActivity extends AppCompatActivity {

    private static final String TAG = "EDIT_TASK";
    private static final String TAG_TEP = "TEP_DEBUG";
    private static final int PICK_FILE_REQUEST_CODE = 999;

    private Task currentTask; // Giữ task đang chỉnh sửa

    private int taskId;
    private EditText edtTitle, edtDescription;
    private Spinner spinnerStatus;
    private SeekBar seekPriority;
    private TextView tvPriorityLevel, tvAssignee, tvDueDate;
    private LinearLayout checklistContainer, attachmentContainer;
    private TextView btnSave;
    private ImageView btnCancel;
    private Button btnAddAttachment;

    private Integer selectedAssigneeId = null;
    private List<Checklist> checklistList = new ArrayList<>();
    private List<Attachment> attachmentList = new ArrayList<>();
    private int checklistIndex = 0;

    private final String[] statusDisplay = {"Chờ xử lý", "Đang làm", "Hoàn thành"};
    private final String[] statusValues = {"cho_xu_ly", "dang_lam", "hoan_thanh"};
    private final String[] priorityDisplay = {"Thấp", "Trung bình", "Cao"};
    private final String[] priorityValues = {"thap", "trung_binh", "cao"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskId = getIntent().getIntExtra("task_id", -1);
        Log.d(TAG, "🆔 Task ID: " + taskId);

        edtTitle = findViewById(R.id.edtTaskTitle);
        edtDescription = findViewById(R.id.edtDescription);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        seekPriority = findViewById(R.id.seekPriority);
        tvPriorityLevel = findViewById(R.id.tvPriorityLevel);
        tvAssignee = findViewById(R.id.tvAssignee);
        tvDueDate = findViewById(R.id.tvDueDate);
        checklistContainer = findViewById(R.id.checklistContainer);
        attachmentContainer = findViewById(R.id.attachmentContainer);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnAddAttachment = findViewById(R.id.btnAddAttachment);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusDisplay);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        seekPriority.setMax(2);
        seekPriority.setProgress(1);
        tvPriorityLevel.setText(priorityDisplay[1]);
        seekPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPriorityLevel.setText(priorityDisplay[progress]);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            Log.d(TAG, "📤 Save button clicked");
            saveTask();
        });
        findViewById(R.id.btnAddChecklist).setOnClickListener(v -> {
            String title = "Checklist" + checklistIndex++;
            Checklist newItem = new Checklist(taskId, title);
            checklistList.add(newItem);
            addChecklistItemView(newItem);
        });
        btnAddAttachment.setOnClickListener(v -> selectFile());
        tvDueDate.setOnClickListener(v -> showDateDialog());
        tvAssignee.setOnClickListener(v -> showAssigneeDialog());

        if (taskId != -1) {
            loadTaskData(taskId);
            loadAttachments(taskId);
        }
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Chọn tệp PDF"), PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            Log.d(TAG_TEP, "📤 Chọn tệp: " + fileUri);
            if (fileUri != null) UploadHelper.uploadAttachment(this, fileUri, taskId, attachment -> {
                attachmentList.add(attachment);
                addAttachmentView(attachment);
            });
        }
    }

    private void loadAttachments(int taskId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getAttachmentsByTask(taskId).enqueue(new Callback<List<Attachment>>() {
            public void onResponse(Call<List<Attachment>> call, Response<List<Attachment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    attachmentList = response.body();
                    for (Attachment item : attachmentList) addAttachmentView(item);
                    Log.d(TAG_TEP, "✅ Load " + attachmentList.size() + " tệp đính kèm");
                } else {
                    Log.d(TAG_TEP, "⚠️ Không có tệp nào hoặc lỗi khi tải");
                }
            }
            public void onFailure(Call<List<Attachment>> call, Throwable t) {
                Log.e(TAG_TEP, "❌ Lỗi tải danh sách tệp: " + t.getMessage());
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

    private void loadTaskData(int taskId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getTaskById(taskId).enqueue(new Callback<Task>() {
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTask = response.body();
                    Log.d(TAG, "📥 Task loaded: " + currentTask.getTieu_de());

                    edtTitle.setText(currentTask.getTieu_de());
                    edtDescription.setText(currentTask.getMo_ta());

                    selectedAssigneeId = currentTask.getMaNguoiGiao();
                    String assignee = currentTask.getNguoiGiao();
                    tvAssignee.setText(assignee != null && !assignee.trim().isEmpty() ? assignee : "Not assigned");

                    tvDueDate.setText(currentTask.getHan_hoan_thanh());

                    for (int i = 0; i < statusValues.length; i++) {
                        if (statusValues[i].equals(currentTask.getTrang_thai())) {
                            spinnerStatus.setSelection(i);
                            break;
                        }
                    }

                    for (int i = 0; i < priorityValues.length; i++) {
                        if (priorityValues[i].equalsIgnoreCase(currentTask.getDo_uu_tien())) {
                            seekPriority.setProgress(i);
                            tvPriorityLevel.setText(priorityDisplay[i]);
                            break;
                        }
                    }

                    checklistList.clear();
                    checklistContainer.removeAllViews();
                    loadChecklist(currentTask.getMa_cv());
                } else {
                    Log.e(TAG, "⚠️ Không lấy được task: " + response.message());
                }
            }

            public void onFailure(Call<Task> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi load task: " + t.getMessage());
            }
        });
    }

    private void loadChecklist(int taskId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getChecklistByTask(taskId).enqueue(new Callback<List<Checklist>>() {
            public void onResponse(Call<List<Checklist>> call, Response<List<Checklist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Checklist item : response.body()) {
                        checklistList.add(item);
                        addChecklistItemView(item);
                    }
                    checklistIndex = checklistList.size();
                    Log.d(TAG, "✅ Checklist loaded: " + checklistIndex + " items");
                }
            }

            public void onFailure(Call<List<Checklist>> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi load checklist: " + t.getMessage());
            }
        });
    }

    private void addChecklistItemView(Checklist checklist) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_checklist_row, checklistContainer, false);
        CheckBox cb = row.findViewById(R.id.cbChecklist);
        EditText edt = row.findViewById(R.id.edtChecklist);
        ImageView btnDelete = row.findViewById(R.id.btnDeleteChecklist);

        cb.setText(checklist.getNoiDung());
        cb.setChecked(checklist.isDaHoanThanh());
        edt.setText(checklist.getNoiDung());

        cb.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime < 400) {
                    cb.setVisibility(View.GONE);
                    edt.setVisibility(View.VISIBLE);
                    edt.requestFocus();
                    edt.setSelection(edt.getText().length());
                }
                lastClickTime = now;
            }
        });

        edt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newText = edt.getText().toString().trim();
                if (!newText.isEmpty()) {
                    checklist.setNoiDung(newText);
                    cb.setText(newText);
                }
                edt.setVisibility(View.GONE);
                cb.setVisibility(View.VISIBLE);
            }
        });

        cb.setOnCheckedChangeListener((buttonView, isChecked) -> checklist.setDaHoanThanh(isChecked));

        btnDelete.setVisibility(View.VISIBLE);
        btnDelete.setOnClickListener(v -> {
            checklistContainer.removeView(row);
            checklistList.remove(checklist);
        });

        checklistContainer.addView(row);
    }

    private void saveTask() {
        SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        int currentUserId = prefs.getInt("userId", -1);

        Task updatedTask = new Task();
        updatedTask.setMa_cv(taskId);
        updatedTask.setTao_boi(currentUserId);

        if (currentTask != null) {
            updatedTask.setMa_nhom(currentTask.getMa_nhom());
            updatedTask.setMa_thu_muc(currentTask.getMa_thu_muc());
        } else {
            Log.e(TAG, "❌ currentTask is null, không thể lấy ma_nhom / ma_thu_muc");
            Toast.makeText(this, "Lỗi: Dữ liệu task không đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = edtTitle.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();
        String status = statusValues[spinnerStatus.getSelectedItemPosition()];
        String priority = priorityValues[seekPriority.getProgress()];
        String deadline = tvDueDate.getText().toString();

        Log.d(TAG, "📤 Save button clicked");
        Log.d(TAG, "📋 Title: " + title);
        Log.d(TAG, "📋 Description: " + desc);
        Log.d(TAG, "📋 Status: " + status);
        Log.d(TAG, "📋 Priority: " + priority);
        Log.d(TAG, "📋 Deadline: " + deadline);
        Log.d(TAG, "📋 Assignee ID: " + selectedAssigneeId);

        updatedTask.setTieu_de(title);
        updatedTask.setMo_ta(desc);
        updatedTask.setTrang_thai(status);
        updatedTask.setDo_uu_tien(priority);
        updatedTask.setHan_hoan_thanh(deadline);
        updatedTask.setMaNguoiGiao(selectedAssigneeId);

        Gson gson = new Gson();
        String json = gson.toJson(updatedTask);
        Log.d(TAG, "📦 JSON gửi lên backend: " + json);

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.updateTask(taskId, updatedTask).enqueue(new Callback<GeneralResponse>() {
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Task updated");
                    saveChecklistItems();
                } else {
                    Log.e(TAG, "❌ Task update failed | code: " + response.code());
                }
            }

            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi update task: " + t.getMessage());
            }
        });
    }




    private void saveChecklistItems() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        for (Checklist item : checklistList) {
            if (item.getMaItem() == 0) {
                api.addChecklistItem(item).enqueue(new Callback<GeneralResponse>() {
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        Log.d(TAG, "✅ Checklist added");
                    }

                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Log.e(TAG, "❌ Lỗi thêm checklist: " + t.getMessage());
                    }
                });
            } else {
                api.updateChecklistContent(item.getMaItem(), item).enqueue(new Callback<GeneralResponse>() {
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                        Log.d(TAG, "✅ Checklist updated");
                    }

                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        Log.e(TAG, "❌ Lỗi cập nhật checklist: " + t.getMessage());
                    }
                });
            }
        }
        Toast.makeText(this, "✅ Đã lưu công việc", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    tvDueDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showAssigneeDialog() {
        // Lấy assignerId và token từ SharedPreferences
        int assignerId = getSharedPreferences("USER_PREF", MODE_PRIVATE).getInt("userId", -1);
        String token = getSharedPreferences("USER_PREF", MODE_PRIVATE).getString("token", null);

        Log.d(TAG, "🔐 Token: " + token + " | 👤 assignerId: " + assignerId);

        if (token == null || assignerId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truyền đầy đủ 4 tham số
        AssignTaskDialog dialog = new AssignTaskDialog(this, taskId, assignerId, token);
        dialog.show();
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view instanceof EditText) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            float x = ev.getRawX() + view.getLeft() - location[0];
            float y = ev.getRawY() + view.getTop() - location[1];

            if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                    (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())) {
                view.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
