package com.mnghiem.projectmanager.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.mnghiem.projectmanager.R;
import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Project;
import com.mnghiem.projectmanager.utils.PrefsUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateWorkspaceBottomSheet extends BottomSheetDialogFragment {

    private EditText edtWorkspaceName;
    private Button btnCreate;
    private TextView btnCancel;

    private static final String TAG = "CREATE_WS";

    public interface WorkspaceCreateListener {
        void onWorkspaceCreated(Project newProject);
    }

    private WorkspaceCreateListener listener;

    public void setWorkspaceCreateListener(WorkspaceCreateListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_create_workspace, container, false);

        edtWorkspaceName = view.findViewById(R.id.edtWorkspaceName);
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnCreate.setOnClickListener(v -> {
            String name = edtWorkspaceName.getText().toString().trim();
            Log.d(TAG, "Click tạo workspace với tên: " + name);
            if (!name.isEmpty()) {
                createWorkspaceSafe(name);
            } else {
                edtWorkspaceName.setError("Enter workspace name");
                Log.w(TAG, "Tên workspace rỗng!");
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private void createWorkspaceSafe(String name) {
        String token = PrefsUtil.getToken(getContext());
        Log.d(TAG, "Token sử dụng: " + token);

        Project project = new Project(name, "");
        Log.d(TAG, "Dữ liệu gửi lên: tên = " + name + ", mô tả = \"\"");

        MyAPI api = APIClient.getClient().create(MyAPI.class);
        api.createProjectSafe("Bearer " + token, project).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                Log.d(TAG, "API trả về code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Project newProject = response.body();

                    try {
                        String json = new Gson().toJson(newProject);
                        Log.d(TAG, "🧾 JSON server trả về: " + json);
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi in JSON", e);
                    }

                    Log.d(TAG, "Tạo workspace thành công: " + newProject.getTenNhom());
                    Log.d(TAG, "🔁 Project ID = " + newProject.getMaNhom());

                    if (listener != null) {
                        listener.onWorkspaceCreated(newProject); // ✅ GỌI TRƯỚC
                    } else {
                        Log.e(TAG, "❌ Listener bị null, không thể gửi dữ liệu project");
                    }

                    dismiss(); // ✅ GỌI SAU listener
                } else {
                    Toast.makeText(getContext(), "Tạo thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Tạo thất bại! Response null hoặc không thành công.");
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Không đọc được error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi kết nối khi tạo workspace", t);
            }
        });
    }
}
