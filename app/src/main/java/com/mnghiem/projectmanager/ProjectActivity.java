package com.mnghiem.projectmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProjectActivity extends AppCompatActivity {

    private LinearLayout workspaceList;
    private int groupId;
    private int currentUserId;
    private String boardTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project); // vẫn dùng layout cũ

        workspaceList = findViewById(R.id.workspaceList);

        // Nhận dữ liệu từ intent
        boardTitle = getIntent().getStringExtra("boardTitle");
        groupId = getIntent().getIntExtra("ma_nhom", -1);
        currentUserId = getIntent().getIntExtra("currentUserId", -1); // 🔥 lấy thêm userId

        Log.d("PROJECT_DETAIL", "🟢 Mở project detail: title=" + boardTitle + ", groupId=" + groupId + ", userId=" + currentUserId);

        // Tiêu đề workspace
        TextView tv = new TextView(this);
        tv.setText(boardTitle + " (ID: " + groupId + ", User: " + currentUserId + ")");
        tv.setTextSize(18);
        tv.setTextColor(0xFF000000);
        tv.setPadding(0, 24, 0, 12);
        workspaceList.addView(tv);

        // Demo layout panel (giữ nguyên như bạn làm mẫu)
        LinearLayout panelRow = new LinearLayout(this);
        panelRow.setOrientation(LinearLayout.HORIZONTAL);
        panelRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        panelRow.addView(createPanelCard("Thiết kế UI", "6 lists | 30 cards", 0xFFE0F7FA));
        panelRow.addView(createPanelCard("Phân quyền", "4 lists", 0xFFE8F5E9));

        workspaceList.addView(panelRow);
    }

    private LinearLayout createPanelCard(String title, String desc, int bgColor) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(bgColor);
        card.setPadding(24, 24, 24, 24);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        params.setMargins(8, 0, 8, 32);
        card.setLayoutParams(params);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(desc);
        tvDesc.setTextSize(12);
        tvDesc.setTextColor(0xFF555555);

        card.addView(tvTitle);
        card.addView(tvDesc);

        // Sau này có thể mở chi tiết thư mục (board) trong workspace
        card.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectActivity.this, BoardDetailActivity.class);
            intent.putExtra("board_title", title);
            intent.putExtra("board_id", -1); // nếu có id cụ thể thì truyền
            intent.putExtra("group_id", groupId);
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        return card;
    }
}
