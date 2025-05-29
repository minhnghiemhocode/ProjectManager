package com.mnghiem.projectmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtTaskTitle, edtDescription;
    private TextView tvStartDate, tvDueDate;
    private ImageButton btnSelectPeople;
    private ImageView btnCancel;
    private TextView btnSave;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar dueCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        edtTaskTitle = findViewById(R.id.edtTaskTitle);
        edtDescription = findViewById(R.id.edtDescription);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvDueDate = findViewById(R.id.tvDueDate);
        btnSelectPeople = findViewById(R.id.btnSelectPeople);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());

        // Save button
        btnSave.setOnClickListener(v -> {
            String title = edtTaskTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề task", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Xử lý lưu task
            Toast.makeText(this, "Đã lưu task: " + title, Toast.LENGTH_SHORT).show();
            finish();
        });

        // Chọn người (hiển thị popup sau)
        btnSelectPeople.setOnClickListener(v -> {
            // TODO: Mở BottomSheet hoặc Dialog chọn người
            Toast.makeText(this, "Chọn người thực hiện", Toast.LENGTH_SHORT).show();
        });

        // Ngày bắt đầu
        tvStartDate.setOnClickListener(v -> pickDateTime(startCalendar, tvStartDate));

        // Hạn chót
        tvDueDate.setOnClickListener(v -> pickDateTime(dueCalendar, tvDueDate));
    }

    private void pickDateTime(Calendar calendar, TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Sau khi chọn ngày → chọn giờ
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (TimePicker view1, int hourOfDay, int minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                String dateTimeStr = String.format("%02d/%02d %02d:%02d",
                                        calendar.get(Calendar.DAY_OF_MONTH),
                                        calendar.get(Calendar.MONTH) + 1,
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE));
                                textView.setText(dateTimeStr);

                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
