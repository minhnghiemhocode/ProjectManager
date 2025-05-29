package com.mnghiem.projectmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
    private TextView tvBoardTitle, tvVisibility;
    private LinearLayout avatarsContainer, pinnedContainer, boardContainer;
    private int groupId;
    private int currentUserId = -1;
    private int creatorId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);
        tvBoardTitle = findViewById(R.id.tvBoardTitle);
        tvVisibility = findViewById(R.id.tvVisibility);
        avatarsContainer = findViewById(R.id.avatarsContainer);
        pinnedContainer = findViewById(R.id.pinnedContainer);
        boardContainer = findViewById(R.id.boardContainer);

        btnBack.setOnClickListener(v -> finish());
        btnMenu.setOnClickListener(this::showPopupMenu);
        tvBoardTitle.setText("Workspace Detail");
        tvVisibility.setText("Public");

        groupId = getIntent().getIntExtra("ma_nhom", -1);
        currentUserId = getSharedPreferences("USER_PREF", MODE_PRIVATE).getInt("userId", -1);
        Log.d("DEBUG_PROJECT_FLOW", "🟢 ProjectDetailActivity nhận: groupId=" + groupId + ", currentUserId=" + currentUserId);
        if (groupId != -1) {
            loadBoardsByGroup(groupId);
            getGroupCreator(groupId);
        }
    }

    private void loadBoardsByGroup(int groupId) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.getBoardsByGroup(groupId).enqueue(new Callback<List<Board>>() {
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 40);
        view.setLayoutParams(params);

        TextView tvTitle = view.findViewById(R.id.tvBoardTitle);
        TextView tvDesc = view.findViewById(R.id.tvBoardDescription);
        TextView tvTaskStat = view.findViewById(R.id.tvTaskStat);
        LinearLayout bg = view.findViewById(R.id.bgBoard);
        ImageView btnMore = view.findViewById(R.id.btnMore);

        tvTitle.setText(board.getTitle());
        tvDesc.setText(board.getDescription());
        tvTaskStat.setText(board.getSoTask() + " tasks");

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(board.getBackgroundColor()), Color.WHITE}
        );
        gradient.setCornerRadius(40f);
        bg.setBackground(gradient);
        bg.setElevation(12f);

        btnMore.setOnClickListener(v -> showBoardMenu(v, board));

        // 👉 Khi nhấn vào toàn bộ card sẽ mở BoardDetailActivity
        view.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectDetailActivity.this, BoardDetailActivity.class);
            intent.putExtra("board_id", board.getBoardId());
            intent.putExtra("board_title", board.getTitle());
            intent.putExtra("group_id", groupId);
            intent.putExtra("currentUserId", currentUserId);
            Log.d("DEBUG_PROJECT_FLOW", "➡️ Mở BoardDetailActivity với boardId=" + board.getBoardId()
                    + ", groupId=" + groupId + ", userId=" + currentUserId);
            startActivity(intent);
        });

        return view;
    }


    private void showBoardMenu(View anchor, Board board) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Sửa");
        popup.getMenu().add("Đổi màu");
        popup.getMenu().add(board.isPinned() ? "Bỏ ghim" : "Ghim");
        popup.getMenu().add("Xoá");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Sửa":
                    showEditBoardDialog(board);
                    break;
                case "Đổi màu":
                    showColorPicker(board);
                    break;
                case "Ghim":
                case "Bỏ ghim":
                    toggleBoardPin(board);
                    break;
                case "Xoá":
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
                .setTitle("Sửa board")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newTitle = edtTitle.getText().toString().trim();
                    String newDesc = edtDesc.getText().toString().trim();

                    if (!newTitle.equals(board.getTitle()) || !newDesc.equals(board.getDescription())) {
                        board.setTitle(newTitle);
                        board.setDescription(newDesc);
                        updateBoardInfo(board);
                    }
                })
                .setNegativeButton("Huỷ", null)
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
                .setTitle("Xoá board")
                .setMessage("Bạn có chắc muốn xoá board này?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteBoard(board.getBoardId()))
                .setNegativeButton("Huỷ", null)
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
        String[] colors = {"#FF8A65", "#4CAF50", "#2196F3", "#FFD600", "#AB47BC"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn màu")
                .setItems(colors, (dialog, which) -> updateBoardColor(board.getBoardId(), colors[which]))
                .show();
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

        // Nếu không phải creator, ẩn hai chức năng đặc biệt
        //if (currentUserId != creatorId) {
        //    popup.getMenu().findItem(R.id.menu_delete).setVisible(false);
        //    popup.getMenu().findItem(R.id.menu_invite).setVisible(false);
        //}

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_add_board) {
                startActivity(new Intent(this, AddBoardActivity.class).putExtra("ma_nhom", groupId));
                return true;
            } else if (itemId == R.id.menu_edit) {
                showEditWorkspaceDialog();
                return true;
            } else if (itemId == R.id.menu_delete) {
                confirmDeleteWorkspace();
                return true;
            } else if (itemId == R.id.menu_invite) {
                new InviteMemberDialog(this, groupId, currentUserId).show();
                return true;
            } else {
                return false;
            }
        });

        popup.show();
    }

    private void showEditWorkspaceDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_board, null);
        EditText edtTitle = dialogView.findViewById(R.id.edtBoardTitle);
        EditText edtDesc = dialogView.findViewById(R.id.edtBoardDescription);

        edtTitle.setText(tvBoardTitle.getText().toString().trim());
        edtDesc.setText(tvVisibility.getText().toString().trim());

        new AlertDialog.Builder(this)
                .setTitle("Sửa workspace")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newTitle = edtTitle.getText().toString().trim();
                    String newDesc = edtDesc.getText().toString().trim();

                    boolean changed = !newTitle.equals(tvBoardTitle.getText().toString()) ||
                            !newDesc.equals(tvVisibility.getText().toString());

                    if (changed) {
                        tvBoardTitle.setText(newTitle);
                        tvVisibility.setText(newDesc);

                        MyAPI api = APIClient.getClient().create(MyAPI.class);
                        Project updated = new Project(newTitle, newDesc);
                        api.updateGroup(groupId, updated).enqueue(new Callback<GeneralResponse>() {
                            @Override
                            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(ProjectDetailActivity.this, "Lỗi khi cập nhật workspace", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                                Toast.makeText(ProjectDetailActivity.this, "Không thể cập nhật workspace", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void confirmDeleteWorkspace() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá nhóm")
                .setMessage("Bạn có chắc chắn muốn xoá nhóm này không?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteWorkspace())
                .setNegativeButton("Huỷ", null)
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
