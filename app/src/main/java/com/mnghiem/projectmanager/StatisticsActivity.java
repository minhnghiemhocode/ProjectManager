package com.mnghiem.projectmanager;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mnghiem.projectmanager.ai.OpenRouterClient;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.*;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    private ImageView btnBack, btnAI;
    private TextView tvTotalTask, tvCompletedTask, tvLateTask;
    private TextView tvStartDate, tvEndDate;
    private PieChart chartOverview;
    private BarChart chartUser;

    private int userId = -1;
    private String overviewStartDate, overviewEndDate;
    private String userStartDate, userEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initViews();

        SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            Log.e(TAG, "❌ Không tìm thấy userId");
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        btnAI.setOnClickListener(v -> fetchAIAdvice());

        tvStartDate.setOnClickListener(v -> pickDate(true));
        tvEndDate.setOnClickListener(v -> pickDate(false));

        setupDefaultDates();
        fetchOverviewStats();
        fetchUserStats();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAI = findViewById(R.id.btnAI);
        tvTotalTask = findViewById(R.id.tvTotalTask);
        tvCompletedTask = findViewById(R.id.tvCompletedTask);
        tvLateTask = findViewById(R.id.tvLateTask);
        chartOverview = findViewById(R.id.chartOverview);
        chartUser = findViewById(R.id.chartUser);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
    }

    private void setupDefaultDates() {
        Calendar cal = Calendar.getInstance();
        overviewEndDate = userEndDate = formatDate(cal);
        tvEndDate.setText(userEndDate);

        cal.add(Calendar.YEAR, -1);
        overviewStartDate = userStartDate = formatDate(cal);
        tvStartDate.setText(userStartDate);
    }

    private String formatDate(Calendar cal) {
        return String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
    }

    private void pickDate(boolean isStart) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String selected = String.format("%04d-%02d-%02d", year, month + 1, day);
                    if (isStart) {
                        userStartDate = selected;
                        tvStartDate.setText(selected);
                    } else {
                        userEndDate = selected;
                        tvEndDate.setText(selected);
                    }
                    fetchUserStats();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void fetchOverviewStats() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getOverview(userId, overviewStartDate, overviewEndDate)
                .enqueue(new Callback<Statistic>() {
                    @Override
                    public void onResponse(Call<Statistic> call, Response<Statistic> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Statistic s = response.body();
                            int total = s.getTong();
                            int completed = s.getHoan_thanh();
                            int notCompleted = total - completed;

                            tvTotalTask.setText("Total: " + total);
                            tvCompletedTask.setText("Completed: " + completed);
                            tvLateTask.setText("Uncompleted: " + notCompleted);

                            drawPieChart(completed, notCompleted);
                        } else {
                            Log.e(TAG, "❌ Overview response null");
                        }
                    }

                    @Override
                    public void onFailure(Call<Statistic> call, Throwable t) {
                        Log.e(TAG, "❌ Lỗi overview: " + t.getMessage());
                    }
                });
    }

    private void fetchUserStats() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getUserDetailStats(userId, userStartDate, userEndDate)
                .enqueue(new Callback<Statistic>() {
                    @Override
                    public void onResponse(Call<Statistic> call, Response<Statistic> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Statistic s = response.body();
                            int completed = s.getHoan_thanh();
                            int overdue = s.getTre_han();
                            int inProgress = s.getTong() - completed - overdue;
                            drawBarChart(completed, overdue, inProgress);
                        }
                    }

                    @Override
                    public void onFailure(Call<Statistic> call, Throwable t) {
                        Log.e(TAG, "❌ Lỗi thống kê người dùng: " + t.getMessage());
                    }
                });
    }

    private void fetchAIAdvice() {
        Log.d(TAG, "🚀 Bắt đầu gọi API AI với userId = " + userId);

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getAIData(userId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                Log.d(TAG, "📥 Nhận được response từ API AI, code = " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tasks = response.body();
                    Log.d(TAG, "📋 Số task nhận được từ API: " + tasks.size());

                    if (tasks.isEmpty()) {
                        Log.e(TAG, "❌ Không có task nào trong response.");
                        return;
                    }

                    StringBuilder prompt = new StringBuilder("Tôi có các công việc sau:\n");
                    for (int i = 0; i < tasks.size(); i++) {
                        Task t = tasks.get(i);
                        String title = t.getTieu_de();
                        String status = t.getTrang_thai();
                        String priority = t.getDo_uu_tien();

                        Log.d(TAG, "📝 Task " + (i + 1) + ": " + title + " | Trạng thái: " + status + " | Ưu tiên: " + priority);

                        prompt.append("- ").append(title)
                                .append(" (Status: ").append(status)
                                .append(", Prority: ").append(priority)
                                .append(")\n");
                    }
                    prompt.append("Hãy cho tôi lời khuyên để quản lý thời gian hiệu quả hơn.");

                    Log.d(TAG, "📤 Prompt gửi lên AI:\n" + prompt);

                    OpenRouterClient.askSuggestion(prompt.toString(), BuildConfig.OPENROUTER_API_KEY, new OpenRouterClient.AIListener() {
                        @Override
                        public void onResult(String message) {
                            Log.d(TAG, "✅ Phản hồi từ AI:\n" + message);
                            runOnUiThread(() -> new AlertDialog.Builder(StatisticsActivity.this)
                                    .setTitle("🤖 Gợi ý từ AI")
                                    .setMessage(message)
                                    .setPositiveButton("Đóng", null)
                                    .show());
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "❌ Lỗi khi gọi AI: " + error);
                        }
                    });

                } else {
                    try {
                        String errBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "❌ Response không thành công: code=" + response.code() + " | body=" + errBody);
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Lỗi khi đọc errorBody: " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi khi gọi API AI: " + t.getMessage(), t);
            }
        });
    }



    private void drawPieChart(int completed, int notCompleted) {
        List<PieEntry> entries = new ArrayList<>();
        if (completed > 0) entries.add(new PieEntry(completed, "Completed"));
        if (notCompleted > 0) entries.add(new PieEntry(notCompleted, "Uncompleted"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        chartOverview.setData(new PieData(dataSet));
        chartOverview.setCenterText("Overview");
        chartOverview.setDescription(new Description());
        chartOverview.invalidate();
    }

    private void drawBarChart(int completed, int overdue, int inProgress) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, completed));
        entries.add(new BarEntry(1, overdue));
        entries.add(new BarEntry(2, inProgress));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);

        BarData data = new BarData(dataSet);
        chartUser.setData(data);
        chartUser.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value) {
                    case 0: return "Completed";
                    case 1: return "Overdue";
                    case 2: return "In Progress";
                    default: return "";
                }
            }
        });
        chartUser.setDescription(new Description());
        chartUser.invalidate();
    }
}
