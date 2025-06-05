package com.mnghiem.projectmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Board;
import com.mnghiem.projectmanager.models.ColorRequest;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.Project;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailActivity extends AppCompatActivity {

    private ImageView btnBack, btnMenu;
    private TextView tvBoardTitle, tvBoardDescription;
    private LinearLayout avatarsContainer, pinnedContainer, boardContainer;
    private int groupId, currentUserId = -1, creatorId = -1;
    private String currentToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);
        tvBoardTitle = findViewById(R.id.tvBoardTitle);
        tvBoardDescription = findViewById(R.id.tvBoardDescription);
        avatarsContainer = findViewById(R.id.avatarsContainer);
        pinnedContainer = findViewById(R.id.pinnedContainer);
        boardContainer = findViewById(R.id.boardContainer);

        btnBack.setOnClickListener(v -> finish());
        btnMenu.setOnClickListener(this::showPopupMenu);

        // Nhận dữ liệu từ Intent
        groupId = getIntent().getIntExtra("ma_nhom", -1);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);
        String boardTitle = getIntent().getStringExtra("boardTitle");
        String boardDescription = getIntent().getStringExtra("boardDescription");

        if (boardTitle != null) tvBoardTitle.setText(boardTitle);
        if (boardDescription != null) tvBoardDescription.setText(boardDescription);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        currentToken = prefs.getString("token", null);

        if (groupId != -1) {
            getGroupCreator(groupId);
            loadBoardsByGroup(groupId);
        }
    }

    private void loadBoardsByGroup(int groupId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getBoardsWithTaskCounts(groupId).enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pinnedContainer.removeAllViews();
                    boardContainer.removeAllViews();
                    for (Board board : response.body()) {
                        View card = createBoardCard(board);
                        if (board.isPinned()) {
                            pinnedContainer.addView(card);
                        } else {
                            boardContainer.addView(card);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                Toast.makeText(ProjectDetailActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGroupCreator(int groupId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getGroupCreator(groupId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    creatorId = response.body();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(ProjectDetailActivity.this, "Không lấy được người tạo nhóm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View createBoardCard(Board board) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_board, null);
        LinearLayout bg = view.findViewById(R.id.bgBoard);

// Thêm đoạn này để giới hạn chiều rộng + margin hai bên
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(68, 24, 68, 24);  // khoảng cách giữa các card và lề
        bg.setLayoutParams(layoutParams);

// Gradient nền và bo góc
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(board.getBackgroundColor()), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        bg.setBackground(gradient);
        bg.setElevation(12f);
        bg.setPadding(32, 32, 32, 32);

        TextView tvTitle = view.findViewById(R.id.tvBoardTitle);
        TextView tvDesc = view.findViewById(R.id.tvBoardDescription);
        TextView tvTaskStat = view.findViewById(R.id.tvTaskStat);
        ImageView btnMore = view.findViewById(R.id.btnMore);

        tvTitle.setText(board.getTitle());
        tvDesc.setText(board.getDescription());
        tvTaskStat.setText(board.getSoTask() + " tasks");

        gradient.setColors(new int[]{Color.parseColor(board.getBackgroundColor()), Color.WHITE});

        gradient.setCornerRadius(40f);
        bg.setBackground(gradient);
        bg.setElevation(12f);
        bg.setPadding(32, 32, 32, 32);

        btnMore.setOnClickListener(v -> showBoardMenu(v, board));

        view.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoardDetailActivity.class);
            intent.putExtra("board_id", board.getBoardId());
            intent.putExtra("board_title", board.getTitle());
            intent.putExtra("board_description", board.getDescription());
            intent.putExtra("group_id", groupId);
            intent.putExtra("currentUserId", currentUserId);
            startActivity(intent);
        });

        return view;
    }

    private void showBoardMenu(View anchor, Board board) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Edit folder");
        popup.getMenu().add("Change color");
        popup.getMenu().add(board.isPinned() ? "Unpin folder" : "Pin folder");
        popup.getMenu().add("Delete folder");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Edit folder":
                    showEditBoardDialog(board);
                    break;
                case "Change color":
                    showColorPicker(board);
                    break;
                case "Pin folder":
                case "Unpin folder":
                    toggleBoardPin(board);
                    break;
                case "Delete folder":
                    confirmDeleteBoard(board);
                    break;
            }
            return true;
        });

        popup.show();
    }

    private void toggleBoardPin(Board board) {
        boolean newState = !board.isPinned();
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.updateBoardPin(board.getBoardId(), newState).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) loadBoardsByGroup(groupId);
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showEditBoardDialog(Board board) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_board, null);
        EditText edtTitle = dialogView.findViewById(R.id.edtBoardTitle);
        EditText edtDesc = dialogView.findViewById(R.id.edtBoardDescription);

        edtTitle.setText(board.getTitle());
        edtDesc.setText(board.getDescription());

        new AlertDialog.Builder(this)
                .setTitle("Edit Folder")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    board.setTitle(edtTitle.getText().toString().trim());
                    board.setDescription(edtDesc.getText().toString().trim());
                    updateBoardInfo(board);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateBoardInfo(Board board) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.updateBoard(board.getBoardId(), board).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) loadBoardsByGroup(groupId);
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void confirmDeleteBoard(Board board) {
        new AlertDialog.Builder(this)
                .setTitle("Delete folder")
                .setMessage("Are you sure you want to delete this folder?")
                .setPositiveButton("Delete", (dialog, which) -> deleteBoard(board.getBoardId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteBoard(int boardId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.deleteBoard(boardId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) loadBoardsByGroup(groupId);
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showColorPicker(Board board) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_color_picker, null);
        GridLayout gridLayout = dialogView.findViewById(R.id.colorGrid);

        String[] colors = {
                "#A1887F", "#EF5350", "#F4511E", "#FFB300", "#FFF176", "#FFEB3B",
                "#42A5F5", "#90CAF9", "#B2EBF2", "#4DB6AC", "#4CAF50", "#AED581",
                "#E0E0E0", "#BDBDBD", "#F8BBD0", "#CE93D8", "#9575CD", "#9FA8DA"
        };

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).create();

        for (String hexColor : colors) {
            FrameLayout wrapper = new FrameLayout(this);
            View colorView = new View(this);

            int size = (int) getResources().getDisplayMetrics().density * 40;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            colorView.setLayoutParams(params);
            colorView.setBackground(createCircleDrawable(hexColor));

            colorView.setOnClickListener(v -> {
                updateBoardColor(board.getBoardId(), hexColor);
                alertDialog.dismiss();
            });

            wrapper.setPadding(8, 8, 8, 8);
            wrapper.addView(colorView);
            gridLayout.addView(wrapper);
        }

        alertDialog.show();
    }

    private GradientDrawable createCircleDrawable(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(colorHex));
        drawable.setSize(100, 100);
        return drawable;
    }


    private void updateBoardColor(int boardId, String color) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.updateBoardColor(boardId, new ColorRequest(color)).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) loadBoardsByGroup(groupId);
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.popup_workspace_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_add_board) {
                startActivity(new Intent(this, AddBoardActivity.class).putExtra("ma_nhom", groupId));
                return true;
            } else if (id == R.id.menu_edit) {
                showEditWorkspaceDialog();
                return true;
            } else if (id == R.id.menu_delete) {
                confirmDeleteWorkspace();
                return true;
            } else if (id == R.id.menu_invite) {
                new InviteMemberDialog(this, groupId, currentUserId, currentToken).show();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showEditWorkspaceDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_board, null);
        EditText edtTitle = dialogView.findViewById(R.id.edtBoardTitle);
        EditText edtDesc = dialogView.findViewById(R.id.edtBoardDescription);

        edtTitle.setText(tvBoardTitle.getText().toString().trim());
        edtDesc.setText(tvBoardDescription.getText().toString().trim());

        new AlertDialog.Builder(this)
                .setTitle("Edit project")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    tvBoardTitle.setText(edtTitle.getText().toString().trim());
                    tvBoardDescription.setText(edtDesc.getText().toString().trim());

                    Project updated = new Project(edtTitle.getText().toString().trim(), edtDesc.getText().toString().trim());
                    MyAPI api = APIClient.getClient().create(MyAPI.class);
                    api.updateGroup(groupId, updated).enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(ProjectDetailActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call, Throwable t) {
                            Toast.makeText(ProjectDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void confirmDeleteWorkspace() {
        new AlertDialog.Builder(this)
                .setTitle("Delete project")
                .setMessage("Are you sure you want to delete this project?")
                .setPositiveButton("Delete", (dialog, which) -> deleteWorkspace())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkspace() {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.deleteGroup(groupId).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProjectDetailActivity.this, "Đã xoá nhóm", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(ProjectDetailActivity.this, "Lỗi xoá nhóm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
