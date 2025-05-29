package com.mnghiem.projectmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Board;
import com.mnghiem.projectmanager.models.GeneralResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBoardActivity extends AppCompatActivity {

    private EditText edtBoardName, edtBoardDesc;
    private TextView tvWorkspaceName, btnSave;
    private int groupId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);

        edtBoardName = findViewById(R.id.edtBoardName);
        edtBoardDesc = findViewById(R.id.edtBoardDesc);
        tvWorkspaceName = findViewById(R.id.tvWorkspaceName);
        btnSave = findViewById(R.id.btnSave);

        groupId = getIntent().getIntExtra("ma_nhom", -1);
        groupName = getIntent().getStringExtra("ten_nhom");
        if (groupName != null) tvWorkspaceName.setText("Workspace: " + groupName);

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String name = edtBoardName.getText().toString().trim();
            String desc = edtBoardDesc.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Vui lòng nhập tên board", Toast.LENGTH_SHORT).show();
                return;
            }

            addBoard(name, desc);
        });
    }

    private void addBoard(String title, String desc) {
        Board board = new Board();
        board.setTitle(title);
        board.setDescription(desc);
        board.setGroupId(groupId);
        board.setBackgroundColor("#64B5F6"); // màu mặc định

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.createBoard(board).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
                    Toast.makeText(AddBoardActivity.this, "Đã tạo board", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddBoardActivity.this, "Tạo board thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(AddBoardActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
