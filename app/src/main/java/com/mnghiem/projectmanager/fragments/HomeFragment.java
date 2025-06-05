package com.mnghiem.projectmanager.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mnghiem.projectmanager.InviteMemberDialog;
import com.mnghiem.projectmanager.ProjectDetailActivity;
import com.mnghiem.projectmanager.R;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.ColorRequest;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private LinearLayout recentContainer, personalContainer;
    private List<Project> allProjects = new ArrayList<>();
    private int currentUserId = -1;
    private String currentToken = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recentContainer = view.findViewById(R.id.recentContainer);
        personalContainer = view.findViewById(R.id.personalContainer);

        ViewFlipper flipper = view.findViewById(R.id.homeHeaderFlipper);
        flipper.setAutoStart(true);
        flipper.setFlipInterval(5000);
        flipper.startFlipping();

        SharedPreferences prefs = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);
        currentToken = prefs.getString("token", null);

        if (currentUserId != -1) {
            fetchProjectCounts(currentUserId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchProjectCounts(int userId) {
        MyAPI myAPI = APIClient.getClient().create(MyAPI.class);
        myAPI.getProjectCountsByUser(userId).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProjects.clear();
                    allProjects.addAll(response.body());

                    showRecentProjects(); // Vẫn hoạt động bình thường
                    for (Project p : allProjects) {
                        personalContainer.addView(buildProjectCard(p));
                    }
                } else {
                    Log.e("DEBUG_HOME", "❌ Không lấy được project counts: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Log.e("DEBUG_HOME", "❌ Lỗi API getProjectCounts: " + t.getMessage());
            }
        });
    }



    private void fetchPersonalProjects(int userId) {
        MyAPI myAPI = APIClient.getClient().create(MyAPI.class);
        myAPI.getPersonalProjects(userId).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Project> personal = response.body();
                    allProjects.addAll(personal);
                    showRecentProjects();
                    for (Project p : allProjects) {
                        personalContainer.addView(buildProjectCard(p));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Log.e("DEBUG_HOME", "❌ Lỗi khi gọi API nhóm cá nhân: " + t.getMessage());
            }
        });
    }

    private void fetchJoinedProjects(int userId) {
        MyAPI myAPI = APIClient.getClient().create(MyAPI.class);
        myAPI.getJoinedProjects(userId).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Project p : response.body()) {
                        allProjects.add(p);
                        personalContainer.addView(buildProjectCard(p));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Log.e("DEBUG_HOME", "❌ Lỗi khi gọi API nhóm tham gia: " + t.getMessage());
            }
        });
    }

    private void showRecentProjects() {
        SharedPreferences prefs = requireContext().getSharedPreferences("RECENT_PROJECTS", Context.MODE_PRIVATE);
        String saved = prefs.getString("recent_ids", "");
        if (saved.isEmpty()) return;

        List<String> ids = Arrays.asList(saved.split(","));
        for (String idStr : ids) {
            try {
                int id = Integer.parseInt(idStr);
                for (Project p : allProjects) {
                    if (p.getMaNhom() == id) {
                        recentContainer.addView(buildProjectCard(p));
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private View buildProjectCard(Project project) {
        Context context = requireContext();
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(project.getMauNen()), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        card.setBackground(gradient);
        card.setElevation(12f);
        card.setPadding(32, 32, 32, 32);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(24, 24, 24, 24);
        card.setLayoutParams(cardParams);

        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(context);
        title.setText(project.getTenNhom());
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        topRow.addView(title);

        ImageView btnMore = new ImageView(context);
        btnMore.setImageResource(R.drawable.ic_more_vert);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(64, 64);
        btnMore.setLayoutParams(iconParams);
        btnMore.setOnClickListener(v -> showProjectMenu(v, project));
        topRow.addView(btnMore);

        card.addView(topRow);

        if (project.getMoTa() != null && !project.getMoTa().isEmpty()) {
            TextView desc = new TextView(context);
            desc.setText(project.getMoTa());
            desc.setTextSize(13);
            desc.setTextColor(Color.DKGRAY);
            desc.setPadding(0, 8, 0, 8);
            card.addView(desc);
        }

        View divider = new View(context);
        divider.setBackgroundColor(Color.parseColor("#CCCCCC"));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2);
        dividerParams.setMargins(0, 24, 0, 16);
        divider.setLayoutParams(dividerParams);
        card.addView(divider);

        LinearLayout bottomRow = new LinearLayout(context);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setWeightSum(2);
        bottomRow.setPadding(0, 12, 0, 12);

        TextView boardStat = new TextView(context);
        boardStat.setText(project.getSoBoard() + " boards");
        boardStat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        boardStat.setTextSize(14);
        boardStat.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        bottomRow.addView(boardStat);

        TextView taskStat = new TextView(context);
        taskStat.setText(project.getSoTask() + " tasks");
        taskStat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        taskStat.setTextSize(14);
        taskStat.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        bottomRow.addView(taskStat);

        card.addView(bottomRow);

        card.setOnClickListener(v -> {
            openBoardDetail(project.getTenNhom(), project.getMoTa(), project.getMaNhom());
        });

        return card;
    }

    private void openBoardDetail(String boardTitle, String boardDesc, int maNhom) {
        saveRecentProject(maNhom);

        Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
        intent.putExtra("boardTitle", boardTitle);
        intent.putExtra("boardDescription", boardDesc);
        intent.putExtra("ma_nhom", maNhom);
        intent.putExtra("currentUserId", currentUserId);
        startActivity(intent);
    }

    private void saveRecentProject(int maNhom) {
        SharedPreferences prefs = requireContext().getSharedPreferences("RECENT_PROJECTS", Context.MODE_PRIVATE);
        String existing = prefs.getString("recent_ids", "");
        List<String> list = new ArrayList<>(Arrays.asList(existing.split(",")));
        list.remove(String.valueOf(maNhom));
        list.add(0, String.valueOf(maNhom));
        while (list.size() > 2) list.remove(2);
        prefs.edit().putString("recent_ids", TextUtils.join(",", list)).apply();
    }

    private void showProjectMenu(View anchor, Project project) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        boolean isOwner = (currentUserId == project.getMaNguoiTao());

        popup.getMenu().add("Edit project");
        popup.getMenu().add("Change color");

        if (isOwner) {
            popup.getMenu().add("Add member");
            popup.getMenu().add("Delete project");
        }

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Edit project":
                    showEditWorkspaceDialog(project);
                    break;
                case "Change color":
                    showColorPicker(project.getMaNhom());
                    break;
                case "Add member":
                    Log.d("DEBUG_TOKEN", "🔐 Token trước khi truyền vào dialog: " + currentToken);
                    new InviteMemberDialog(requireContext(), project.getMaNhom(), currentUserId, currentToken).show();
                    break;
                case "Delete project":
                    confirmDeleteWorkspace(project.getMaNhom());
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void showEditWorkspaceDialog(Project project) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_board, null);
        EditText edtTitle = dialogView.findViewById(R.id.edtBoardTitle);
        EditText edtDesc = dialogView.findViewById(R.id.edtBoardDescription);

        edtTitle.setText(project.getTenNhom());
        edtDesc.setText(project.getMoTa());

        new AlertDialog.Builder(getContext())
                .setTitle("Edit project")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTitle = edtTitle.getText().toString().trim();
                    String newDesc = edtDesc.getText().toString().trim();

                    Project updated = new Project(newTitle, newDesc);
                    MyAPI api = APIClient.getClient().create(MyAPI.class);
                    api.updateGroup(project.getMaNhom(), updated).enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                            if (response.isSuccessful()) requireActivity().recreate();
                            else Toast.makeText(getContext(), "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteWorkspace(int maNhom) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete project")
                .setMessage("Are you sure you want to delete this project?")
                .setPositiveButton("Delete", (dialog, which) -> deleteWorkspace(maNhom))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkspace(int maNhom) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.deleteGroup(maNhom).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xoá nhóm", Toast.LENGTH_SHORT).show();
                    requireActivity().recreate();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi xoá nhóm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showColorPicker(int maNhom) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);
        GridLayout gridLayout = dialogView.findViewById(R.id.colorGrid);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        String[] colors = {
                "#A1887F", "#EF5350", "#F4511E", "#FFB300", "#FFF176", "#FFEB3B",
                "#42A5F5", "#90CAF9", "#B2EBF2", "#4DB6AC", "#4CAF50", "#AED581",
                "#E0E0E0", "#BDBDBD", "#F8BBD0", "#CE93D8", "#9575CD", "#9FA8DA"
        };

        for (String hexColor : colors) {
            FrameLayout wrapper = new FrameLayout(getContext());
            View colorView = new View(getContext());

            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            colorView.setLayoutParams(params);
            colorView.setBackground(createCircleDrawable(hexColor));

            wrapper.addView(colorView);
            wrapper.setPadding(8, 8, 8, 8);

            colorView.setOnClickListener(v -> {
                updateGroupColor(maNhom, hexColor);
                alertDialog.dismiss();
            });

            gridLayout.addView(wrapper);
        }

        alertDialog.show();
    }

    private Drawable createCircleDrawable(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(colorHex));
        drawable.setSize(100, 100);
        return drawable;
    }


    private void updateGroupColor(int maNhom, String newColor) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.updateGroupColor(maNhom, new ColorRequest(newColor)).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Đã đổi màu!", Toast.LENGTH_SHORT).show();
                    requireActivity().recreate();
                } else {
                    Toast.makeText(getContext(), "Lỗi đổi màu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
