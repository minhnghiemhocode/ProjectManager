package com.mnghiem.projectmanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mnghiem.projectmanager.R;
import com.mnghiem.projectmanager.TaskDetailActivity;
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

public class DeadlineFragment extends Fragment {

    private LinearLayout dueSoonContainer, dueLaterContainer;
    private int currentUserId;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deadline, container, false);

        dueSoonContainer = view.findViewById(R.id.dueSoonContainer);
        dueLaterContainer = view.findViewById(R.id.dueLaterContainer);

        currentUserId = requireActivity().getSharedPreferences("USER_PREF", getContext().MODE_PRIVATE).getInt("userId", -1);
        Log.d("DEBUG_DEADLINE", "👉 userId: " + currentUserId);

        if (currentUserId != -1) {
            fetchUserTasks(currentUserId);
        }

        return view;
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
        Context context = getContext();

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(32, 32, 32, 32);
        card.setElevation(12f);

        // Màu nền gradient tùy theo trạng thái
        String color = "#B2EBF2"; // mặc định vàng nhạt
        switch (task.getTrang_thai()) {
            case "hoan_thanh":
                color = "#5EE063"; // xanh nhạt
                break;
            case "dang_lam":
                color = "#B3E5FC"; // xanh dương nhạt
                break;
            case "cho_xu_ly":
                color = "#F0F095"; // tím nhạt
                break;
        }

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(color), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        card.setBackground(gradient);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24, 24, 24, 24);
        card.setLayoutParams(params);

        // Tiêu đề
        TextView title = new TextView(context);
        title.setText(task.getTieu_de());
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        card.addView(title);

        // Trạng thái
        TextView status = new TextView(context);
        String statusLabel;
        switch (task.getTrang_thai()) {
            case "hoan_thanh":
                statusLabel = "Hoàn thành";
                break;
            case "dang_lam":
                statusLabel = "Đang làm";
                break;
            case "cho_xu_ly":
                statusLabel = "Chờ xử lý";
                break;
            default:
                statusLabel = task.getTrang_thai();
        }
        status.setText("Status: " + statusLabel);

        status.setTextColor(Color.DKGRAY);
        status.setTextSize(14);
        status.setPadding(0, 8, 0, 0);
        card.addView(status);

        // Hạn hoàn thành
        TextView deadline = new TextView(context);
        deadline.setText("Duo date: " + task.getHan_hoan_thanh());
        deadline.setTextColor(Color.DKGRAY);
        deadline.setTextSize(14);
        deadline.setPadding(0, 4, 0, 0);
        card.addView(deadline);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getMa_cv());
            intent.putExtra("currentUserId", currentUserId);
            Log.d("DeadlineFragment", "🟢 Open TaskDetailActivity with taskId = " + task.getMa_cv());
            startActivity(intent);
        });

        return card;
    }

}
