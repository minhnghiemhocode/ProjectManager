package com.mnghiem.projectmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Task;

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
    private ImageButton btnSelectPeople;
    private ImageView btnCancel;
    private TextView btnSave;

    private Calendar dueCalendar = Calendar.getInstance();

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
        btnSelectPeople = findViewById(R.id.btnSelectPeople);
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

        btnSelectPeople.setOnClickListener(v ->
                Toast.makeText(this, "Chọn người thực hiện (mặc định là người tạo)", Toast.LENGTH_SHORT).show()
        );
    }

    private void updatePriorityLabel(int value) {
        switch (value) {
            case 0:
                tvPriorityLevel.setText("Low");
                tvPriorityLevel.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                break;
            case 1:
                tvPriorityLevel.setText("Medium");
                tvPriorityLevel.setTextColor(ContextCompat.getColor(this, R.color.orange));
                break;
            case 2:
                tvPriorityLevel.setText("High");
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
        String deadline = tvDueDate.getText().toString().equals("Select date") ? null : tvDueDate.getText().toString();
        String priority = getPriorityString(seekPriority.getProgress());

        if (title.isEmpty()) {
            Toast.makeText(this, "Tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.setTieu_de(title);
        task.setTrang_thai("cho_xu_ly");
        task.setHan_hoan_thanh(deadline);
        task.setDo_uu_tien(priority);
        task.setMo_ta(desc);
        task.setMa_nhom(groupId);
        task.setMa_thu_muc(boardId);
        task.setTao_boi(currentUserId);

        // ✅ In toàn bộ giá trị để kiểm tra
        Log.d("DEBUG_ADD_TASK", "📤 Task gửi đi:");
        Log.d("DEBUG_ADD_TASK", "👉 Tiêu đề: " + task.getTieu_de());
        Log.d("DEBUG_ADD_TASK", "👉 Mô tả: " + task.getMo_ta());
        Log.d("DEBUG_ADD_TASK", "👉 Trạng thái: " + task.getTrang_thai());
        Log.d("DEBUG_ADD_TASK", "👉 Hạn: " + task.getHan_hoan_thanh());
        Log.d("DEBUG_ADD_TASK", "👉 Ưu tiên: " + task.getDo_uu_tien());
        Log.d("DEBUG_ADD_TASK", "👉 Nhóm (ma_nhom): " + task.getMa_nhom());
        Log.d("DEBUG_ADD_TASK", "👉 Thư mục (ma_thu_muc): " + task.getMa_thu_muc());
        Log.d("DEBUG_ADD_TASK", "👉 Người tạo (tao_boi): " + task.getTao_boi());

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.createTask(task).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddTaskActivity.this, "Tạo task thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("DEBUG_ADD_TASK", "❌ Lỗi tạo task. Mã HTTP: " + response.code());
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("DEBUG_ADD_TASK", "🧾 errorBody: " + errorMsg);
                    } catch (Exception e) {
                        Log.e("DEBUG_ADD_TASK", "❌ Không đọc được errorBody", e);
                    }
                    Toast.makeText(AddTaskActivity.this, "Lỗi tạo task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AddTaskActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DEBUG_ADD_TASK", "❌ Lỗi mạng khi gọi API", t);
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
