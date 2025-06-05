package com.mnghiem.projectmanager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

    private static final String TAG = "INVITE_MEMBER";
    private EditText edtEmail;
    private Button btnInvite, btnCancel;
    private final int groupId;
    private final int inviterId;
    private final String token;

    public InviteMemberDialog(@NonNull Context context, int groupId, int inviterId, String token) {
        super(context);
        this.groupId = groupId;
        this.inviterId = inviterId;
        this.token = token;
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
                Log.w(TAG, "⚠️ Email rỗng");
            } else {
                Log.d(TAG, "📤 Đang gửi lời mời tới: " + email);
                sendInvite(email);
            }
        });

        btnCancel.setOnClickListener(v -> {
            Log.d(TAG, "❎ Huỷ gửi lời mời");
            dismiss();
        });
    }

    private void sendInvite(String email) {
        Log.d(TAG, "🚀 Bắt đầu gửi lời mời");
        MyAPI api = APIClient.getClient().create(MyAPI.class);

        InviteRequest request = new InviteRequest(email, groupId, inviterId);

        // Luôn gọi inviteIfExist trước
        Call<GeneralResponse> call = api.inviteUserWithToken(request, "Bearer " + token);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                if (response.isSuccessful()) {
                    showToastAndDismiss(response.body().getMessage());
                } else if (response.code() == 404) {
                    // Nếu không tồn tại → Gọi inviteIfNotExist
                    Log.d(TAG, "⚠️ Email chưa tồn tại, chuyển sang inviteIfNotExist");

                    Call<GeneralResponse> fallbackCall = api.inviteUserWithoutToken(request);
                    fallbackCall.enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(Call<GeneralResponse> call2, Response<GeneralResponse> resp2) {
                            if (resp2.isSuccessful() && resp2.body() != null) {
                                showToastAndDismiss(resp2.body().getMessage());
                            } else {
                                Log.e(TAG, "❌ inviteIfNotExist lỗi: " + resp2.code());
                                Toast.makeText(getContext(), "Không thể gửi lời mời", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call2, Throwable t) {
                            Log.e(TAG, "❌ inviteIfNotExist lỗi kết nối", t);
                            Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "❌ inviteIfExist lỗi: " + response.code());
                    Toast.makeText(getContext(), "Không thể mời người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi kết nối đến API", t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToastAndDismiss(String msg) {
        Toast.makeText(getContext(), "✅ " + msg, Toast.LENGTH_SHORT).show();
        dismiss();
    }

}
