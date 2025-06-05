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
import com.mnghiem.projectmanager.models.AssignRequest;
import com.mnghiem.projectmanager.models.GeneralResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignTaskDialog extends Dialog {

    private static final String TAG = "ASSIGN_TASK";
    private EditText edtEmail;
    private Button btnInvite, btnCancel;
    private final int taskId;
    private final int assignerId;
    private final String token;

    public AssignTaskDialog(@NonNull Context context, int taskId, int assignerId, String token) {
        super(context);
        this.taskId = taskId;
        this.assignerId = assignerId;
        this.token = token;
        Log.d(TAG, "🔧 Constructor - taskId: " + taskId + ", assignerId: " + assignerId + ", token: " + token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_assign_task);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtEmail = findViewById(R.id.edtTaskEmail);
        btnInvite = findViewById(R.id.btnInviteTask);
        btnCancel = findViewById(R.id.btnCancelTask);

        btnInvite.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            Log.d(TAG, "📥 Email nhập: " + email);

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "⚠️ Email rỗng");
            } else {
                Log.d(TAG, "📤 Đang gửi lời mời tới: " + email);
                assignTask(email);
            }
        });

        btnCancel.setOnClickListener(v -> {
            Log.d(TAG, "❎ Huỷ mời thành viên vào task");
            dismiss();
        });
    }

    private void assignTask(String email) {
        Log.d(TAG, "🚀 Bắt đầu gọi assignTaskWithToken()");
        MyAPI api = APIClient.getClient().create(MyAPI.class);
        AssignRequest request = new AssignRequest(email, taskId, assignerId);

        Log.d(TAG, "📦 Payload gửi đi:");
        Log.d(TAG, "   📧 Email: " + email);
        Log.d(TAG, "   🆔 Task ID: " + taskId);
        Log.d(TAG, "   👤 Assigner ID: " + assignerId);
        Log.d(TAG, "   🔐 Token: " + token);

        Call<GeneralResponse> call = api.assignUserWithToken(request, "Bearer " + token);
        call.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                Log.d(TAG, "📥 Phản hồi từ assignUserWithToken - Code: " + response.code());

                if (response.isSuccessful()) {
                    GeneralResponse res = response.body();
                    Log.d(TAG, "✅ assignUserWithToken thành công: " + (res != null ? res.getMessage() : "null"));
                    showToastAndDismiss(res != null ? res.getMessage() : "Phân công thành công");
                } else if (response.code() == 404) {
                    Log.w(TAG, "⚠️ Người dùng chưa tồn tại, thử gửi lời mời (assignUserWithoutToken)");

                    Call<GeneralResponse> fallbackCall = api.assignUserWithoutToken(request);
                    fallbackCall.enqueue(new Callback<GeneralResponse>() {
                        @Override
                        public void onResponse(Call<GeneralResponse> call2, Response<GeneralResponse> resp2) {
                            Log.d(TAG, "📥 Phản hồi từ assignUserWithoutToken - Code: " + resp2.code());

                            if (resp2.isSuccessful() && resp2.body() != null) {
                                Log.d(TAG, "✅ assignUserWithoutToken thành công: " + resp2.body().getMessage());
                                showToastAndDismiss(resp2.body().getMessage());
                            } else {
                                Log.e(TAG, "❌ assignUserWithoutToken thất bại | code: " + resp2.code());
                                Toast.makeText(getContext(), "Không thể mời người dùng (assign fallback)", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralResponse> call2, Throwable t) {
                            Log.e(TAG, "❌ Lỗi mạng khi gọi assignUserWithoutToken", t);
                            Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Log.e(TAG, "❌ assignUserWithToken thất bại | Mã lỗi: " + response.code());
                    try {
                        Log.e(TAG, "❌ Lỗi chi tiết: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Không đọc được lỗi chi tiết", e);
                    }
                    Toast.makeText(getContext(), "Không thể phân công người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.e(TAG, "❌ Lỗi kết nối đến API assignUserWithToken", t);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToastAndDismiss(String msg) {
        Toast.makeText(getContext(), "✅ " + msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "🟢 Dialog đóng lại sau thành công: " + msg);
        dismiss();
    }
}
