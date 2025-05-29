package com.mnghiem.projectmanager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.InviteRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteMemberDialog extends Dialog {

    private EditText edtEmail;
    private Button btnInvite, btnCancel;
    private final int groupId;
    private final int inviterId;

    public InviteMemberDialog(@NonNull Context context, int groupId, int inviterId) {
        super(context);
        this.groupId = groupId;
        this.inviterId = inviterId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_invite_member);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtEmail = findViewById(R.id.edtEmail);
        btnInvite = findViewById(R.id.btnInvite);
        btnCancel = findViewById(R.id.btnCancel);

        btnInvite.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            } else {
                sendInvite(email);
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void sendInvite(String email) {
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        InviteRequest request = new InviteRequest(email, groupId, inviterId);

        api.inviteUser(request).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Đã gửi lời mời", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Không gửi được lời mời", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
