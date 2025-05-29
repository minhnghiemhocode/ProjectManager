package com.mnghiem.projectmanager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Ánh xạ view
        btnBack = findViewById(R.id.btnBack);
        resultList = findViewById(R.id.resultList);

        // Xử lý quay lại
        btnBack.setOnClickListener(v -> onBackPressed());

        // Thêm dữ liệu mẫu – sau này thay bằng kết quả từ DB
        addSearchResult("Project Manager");
        addSearchResult("Flutter Learning");
        addSearchResult("Personal Portfolio Website");
        addSearchResult("UI/UX Design Mobile");
        addSearchResult("Machine Learning Pipeline");
    }

    // Hàm tạo từng kết quả hiển thị
    private void addSearchResult(String projectName) {
        TextView tv = new TextView(this);
        tv.setText(projectName);
        tv.setPadding(24, 24, 24, 24);
        tv.setTextSize(16);
        tv.setTextColor(getColor(android.R.color.black));

        resultList.addView(tv);
    }
}
