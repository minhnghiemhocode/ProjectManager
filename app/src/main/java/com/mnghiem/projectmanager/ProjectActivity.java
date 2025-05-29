package com.mnghiem.projectmanager;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ProjectActivity extends BaseActivity {

    private LinearLayout workspaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        setupTopAndBottomBar();

        workspaceList = findViewById(R.id.workspaceList);

        // Mock dữ liệu workspace + panel
        List<String> projects = List.of("Marketing", "UI Redesign", "AI Planning");

        for (String projectName : projects) {
            addWorkspace(projectName);
        }
    }

    private void addWorkspace(String projectTitle) {
        // Tiêu đề workspace
        TextView tv = new TextView(this);
        tv.setText(projectTitle);
        tv.setTextSize(18);
        tv.setTextColor(0xFF000000);
        tv.setPadding(0, 24, 0, 12);
        workspaceList.addView(tv);

        // Panel layout chứa 2 panel demo
        LinearLayout panelRow = new LinearLayout(this);
        panelRow.setOrientation(LinearLayout.HORIZONTAL);
        panelRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Thêm 2 panel mẫu
        panelRow.addView(createPanelCard("Design Login", "6 lists | 30 cards", 0xFFE0F7FA));
        panelRow.addView(createPanelCard("Team Roles", "4 lists", 0xFFE8F5E9));

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

        // Sự kiện click mở chi tiết (sau này)
        card.setOnClickListener(v -> {
            // TODO: mở chi tiết project/panel
        });

        return card;
    }
}
