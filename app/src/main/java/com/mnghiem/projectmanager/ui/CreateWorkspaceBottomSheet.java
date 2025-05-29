package com.mnghiem.projectmanager.ui;

import android.os.Bundle;
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
            if (!name.isEmpty()) {
                createWorkspace(name);
            } else {
                edtWorkspaceName.setError("Enter workspace name");
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private void createWorkspace(String name) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        String token = PrefsUtil.getToken(getContext());

        Project project = new Project(name, ""); // mô tả rỗng, mặc định riêng tư

        api.createProject("Bearer " + token, project).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful() && listener != null) {
                    listener.onWorkspaceCreated(response.body());
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Tạo thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
