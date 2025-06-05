package com.mnghiem.projectmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeadlineActivity extends BaseActivity {

    private LinearLayout dueSoonContainer, dueLaterContainer;
    private int currentUserId;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline);
        setupTopAndBottomBar();

        dueSoonContainer = findViewById(R.id.dueSoonContainer);
        dueLaterContainer = findViewById(R.id.dueLaterContainer);

        currentUserId = getSharedPreferences("USER_PREF", MODE_PRIVATE).getInt("userId", -1);
        Log.d("DEBUG_DEADLINE", "👉 userId: " + currentUserId);

        if (currentUserId != -1) {
            fetchUserTasks(currentUserId);
        }
    }

    private void fetchUserTasks(int userId) {
        Log.d("DEBUG_DEADLINE", "📤 Gửi yêu cầu lấy task với userId = " + userId);

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getTasksWithDeadlines(userId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tasks = response.body();
                    Log.d("DEBUG_DEADLINE", "✅ Số lượng task nhận được: " + tasks.size());

                    dueSoonContainer.removeAllViews();
                    dueLaterContainer.removeAllViews();

                    for (Task task : tasks) {
                        Log.d("DEBUG_DEADLINE", "📌 Task: " + task.getTieu_de() + " - Hạn: " + task.getHan_hoan_thanh());

                        String deadline = task.getHan_hoan_thanh();
                        if (deadline != null && !deadline.trim().isEmpty()) {
                            long days = daysUntil(deadline);
                            View card = buildTaskCard(task);
                            if (days <= 7) {
                                dueSoonContainer.addView(card);
                            } else {
                                dueLaterContainer.addView(card);
                            }
                        }
                    }
                    Log.d("DEBUG_DEADLINE", "✅ Có " + response.body().size() + " task");
                } else {
                    Log.e("DEBUG_DEADLINE", "❌ response.code = " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("DEBUG_DEADLINE", "❌ response.errorBody = " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e("DEBUG_DEADLINE", "❌ Lỗi khi đọc errorBody: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("DEBUG_DEADLINE", "❌ Lỗi kết nối: " + t.getMessage());
            }
        });
    }


    private long daysUntil(String deadline) {
        try {
            Date now = new Date();
            Date dueDate = sdf.parse(deadline);
            if (dueDate == null) return Long.MAX_VALUE;
            long diff = dueDate.getTime() - now.getTime();
            return diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            return Long.MAX_VALUE;
        }
    }

    private View buildTaskCard(Task task) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        card.setElevation(12f);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#FFF8C4"), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        card.setBackground(gradient);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 40);
        card.setLayoutParams(params);

        TextView title = new TextView(this);
        title.setText(task.getTieu_de());
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        card.addView(title);

        TextView status = new TextView(this);
        status.setText("Trạng thái: " + task.getTrang_thai());
        status.setTextColor(Color.DKGRAY);
        status.setPadding(0, 8, 0, 0);
        card.addView(status);

        TextView deadline = new TextView(this);
        deadline.setText("Hạn: " + task.getHan_hoan_thanh());
        deadline.setTextColor(Color.DKGRAY);
        card.addView(deadline);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getMa_cv());  // 🔥 fix đúng key
            intent.putExtra("currentUserId", currentUserId);
            Log.d("DeadlineActivity", "🟢 Open TaskDetailActivity with taskId = " + task.getMa_cv());
            startActivity(intent);
        });

        return card;
    }
}
