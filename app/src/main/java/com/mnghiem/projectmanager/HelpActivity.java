package com.mnghiem.projectmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {

    private ImageView btnBack;
    private LinearLayout helpContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btnBack = findViewById(R.id.btnBack);
        helpContainer = findViewById(R.id.helpContainer);

        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());

        // Thêm các mục trợ giúp
        addHelp("Giới thiệu ứng dụng", "Project Manager là ứng dụng quản lý dự án giúp người dùng tổ chức công việc, phân công nhiệm vụ và theo dõi tiến độ theo dạng bảng Kanban hoặc danh sách.");
        addHelp("Tạo nhóm làm việc", "Bạn có thể tạo nhóm trong phần 'Nhóm', thêm mô tả và mời các thành viên cùng tham gia quản lý dự án.");
        addHelp("Tạo và giao nhiệm vụ", "Mỗi nhóm có thể tạo nhiều công việc, giao cho các thành viên, đặt hạn hoàn thành và mức độ ưu tiên.");
        addHelp("Theo dõi tiến độ", "Các công việc được phân loại theo trạng thái: chờ xử lý, đang làm, hoàn thành... giúp bạn dễ dàng kiểm soát.");
        addHelp("Tùy chỉnh cá nhân", "Bạn có thể thay đổi ảnh đại diện, bật giao diện tối, đổi ngôn ngữ và nhận thông báo cá nhân theo nhu cầu.");
    }

    private void addHelp(String title, String description) {
        LinearLayout helpItem = new LinearLayout(this);
        helpItem.setOrientation(LinearLayout.VERTICAL);
        helpItem.setPadding(0, 0, 0, 24);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("• " + title);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(0xFF000000);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setFocusable(true);
        tvTitle.setContentDescription("Mục trợ giúp: " + title);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(description);
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(0xFF555555);
        tvDesc.setVisibility(View.GONE);
        tvDesc.setFocusable(true);
        tvDesc.setContentDescription("Nội dung trợ giúp: " + description);

        // Toggle hiển thị khi nhấn tiêu đề
        tvTitle.setOnClickListener(v -> {
            if (tvDesc.getVisibility() == View.VISIBLE) {
                tvDesc.setVisibility(View.GONE);
            } else {
                tvDesc.setVisibility(View.VISIBLE);
            }
        });

        helpItem.addView(tvTitle);
        helpItem.addView(tvDesc);
        helpContainer.addView(helpItem);
    }
}
