package com.mnghiem.projectmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.GeneralResponse;
import com.mnghiem.projectmanager.models.ResendOtpRequest;
import com.mnghiem.projectmanager.models.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerifyOtp;
    private String email;  // nhận từ intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        TextView tvResendOtp = findViewById(R.id.tvResendOtp); // ✅ THÊM DÒNG NÀY

        email = getIntent().getStringExtra("email");

        // Xác minh OTP
        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();

            if (otp.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập mã OTP hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            VerifyOtpRequest request = new VerifyOtpRequest(email, otp);

            MyAPI api = APIClient.getClient().create(MyAPI.class);
            api.verifyOtp(request).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(VerifyOtpActivity.this, "Xác minh thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VerifyOtpActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "Sai mã OTP hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // ✅ Gửi lại OTP
        tvResendOtp.setOnClickListener(v -> {
            MyAPI api = APIClient.getClient().create(MyAPI.class);
            api.resendOtp(new ResendOtpRequest(email)).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(VerifyOtpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "Không thể gửi lại OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
