package com.mnghiem.projectmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtTaskTitle, edtDescription;
    private TextView tvDueDate, tvPriorityLevel;
    private SeekBar seekPriority;
    private ImageView btnCancel;
    private TextView btnSave;

    private final Calendar dueCalendar = Calendar.getInstance();
    private int boardId = -1;
    private int groupId = -1;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        edtTaskTitle = findViewById(R.id.edtTaskTitle);
        edtDescription = findViewById(R.id.edtDescription);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvPriorityLevel = findViewById(R.id.tvPriorityLevel);
        seekPriority = findViewById(R.id.seekPriority);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        boardId = getIntent().getIntExtra("boardId", -1);
        groupId = getIntent().getIntExtra("groupId", -1);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        Log.d("DEBUG_ADD_TASK", "📥 Nhận Intent: boardId=" + boardId + ", groupId=" + groupId + ", userId=" + currentUserId);

        btnCancel.setOnClickListener(v -> finish());
        tvDueDate.setOnClickListener(v -> showDatePicker());

        seekPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePriorityLabel(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void updatePriorityLabel(int value) {
        switch (value) {
            case 0:
                tvPriorityLevel.setText("Thấp");
                tvPriorityLevel.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                break;
            case 1:
                tvPriorityLevel.setText("Trung bình");
                tvPriorityLevel.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            case 2:
                tvPriorityLevel.setText("Cao");
                tvPriorityLevel.setTextColor(ContextCompat.getColor(this, R.color.red));
                break;
        }
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            dueCalendar.set(year, month, day);
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dueCalendar.getTime());
            tvDueDate.setText(formattedDate);
        }, dueCalendar.get(Calendar.YEAR), dueCalendar.get(Calendar.MONTH), dueCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveTask() {
        String title = edtTaskTitle.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();
        String deadline = tvDueDate.getText().toString().equals("Chọn ngày") ? null : tvDueDate.getText().toString();
        String priority = getPriorityString(seekPriority.getProgress());

        if (title.isEmpty()) {
            Toast.makeText(this, "Tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (groupId <= 0 || boardId <= 0 || currentUserId <= 0) {
            Toast.makeText(this, "Thiếu thông tin nhóm, thư mục hoặc người tạo", Toast.LENGTH_LONG).show();
            Log.e("DEBUG_ADD_TASK", "❌ groupId/boardId/userId không hợp lệ: group=" + groupId + ", board=" + boardId + ", user=" + currentUserId);
            return;
        }

        Task task = new Task();
        task.setTieu_de(title);
        task.setMo_ta(desc.isEmpty() ? "" : desc);
        task.setTrang_thai("cho_xu_ly");
        task.setHan_hoan_thanh(deadline);
        task.setDo_uu_tien(priority);
        task.setMa_nhom(groupId);
        task.setMa_thu_muc(boardId);
        task.setTao_boi(currentUserId);

        // Log toàn bộ thông tin chi tiết
        Log.d("DEBUG_ADD_TASK", "📤 GỬI DỮ LIỆU TASK:");
        Log.d("DEBUG_ADD_TASK", "📝 Tiêu đề       : " + task.getTieu_de());
        Log.d("DEBUG_ADD_TASK", "🗒️  Mô tả         : " + task.getMo_ta());
        Log.d("DEBUG_ADD_TASK", "📅 Hạn hoàn thành: " + task.getHan_hoan_thanh());
        Log.d("DEBUG_ADD_TASK", "⭐ Ưu tiên        : " + task.getDo_uu_tien());
        Log.d("DEBUG_ADD_TASK", "📦 Nhóm          : " + task.getMa_nhom());
        Log.d("DEBUG_ADD_TASK", "📦 Thư mục       : " + task.getMa_thu_muc());
        Log.d("DEBUG_ADD_TASK", "👤 Người tạo     : " + task.getTao_boi());

        // In JSON body thật sự gửi lên (rất hữu ích để debug backend)
        Gson gson = new Gson();
        Log.d("DEBUG_ADD_TASK", "📦 JSON gửi lên:\n" + gson.toJson(task));

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.createTask(task).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddTaskActivity.this, "✅ Tạo task thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("DEBUG_ADD_TASK", "❌ Lỗi tạo task. Mã HTTP: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorMsg = response.errorBody().string();
                            Log.e("DEBUG_ADD_TASK", "🧾 Lỗi từ server: " + errorMsg);
                            Toast.makeText(AddTaskActivity.this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Log.e("DEBUG_ADD_TASK", "⚠️ Không đọc được nội dung lỗi", e);
                        Toast.makeText(AddTaskActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.e("DEBUG_ADD_TASK", "❌ Lỗi mạng khi gọi API", t);
                Toast.makeText(AddTaskActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getPriorityString(int level) {
        switch (level) {
            case 0: return "thap";
            case 1: return "trung_binh";
            case 2: return "cao";
            default: return "trung_binh";
        }
    }
}
