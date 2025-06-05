package com.mnghiem.projectmanager.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.mnghiem.projectmanager.api.APIClient;
import com.mnghiem.projectmanager.api.MyAPI;
import com.mnghiem.projectmanager.models.Attachment;
import com.mnghiem.projectmanager.models.GeneralResponse;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadHelper {

    public interface AttachmentCallback {
        void onUploaded(Attachment attachment);
    }

    public static void uploadAttachment(Context context, Uri uri, int taskId, AttachmentCallback callback) {
        Log.d("TEP_DEBUG", "🔁 Bắt đầu upload attachment");

        try {
            String fileName = getFileName(context, uri);
            Log.d("TEP_DEBUG", "📄 File name: " + fileName);

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e("TEP_DEBUG", "❌ Không thể mở InputStream");
                return;
            }

            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);

            SharedPreferences prefs = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);

            RequestBody taskIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(taskId));
            RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

            MyAPI api = APIClient.getClient().create(MyAPI.class);
            api.uploadAttachment(body, taskIdPart, userIdPart).enqueue(new Callback<GeneralResponse>() {
                @Override
                public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        String fileUrl = response.body().getMessage();
                        Log.d("TEP_DEBUG", "✅ Upload thành công: " + fileUrl);

                        Attachment attachment = new Attachment();
                        attachment.setDuongDan(fileUrl);
                        attachment.setMaCv(taskId);
                        attachment.setTaiLenBoi(userId);

                        if (callback != null) {
                            callback.onUploaded(attachment);
                        }
                    } else {
                        Log.e("TEP_DEBUG", "❌ Upload thất bại: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponse> call, Throwable t) {
                    Log.e("TEP_DEBUG", "❌ Lỗi upload: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("TEP_DEBUG", "❌ Exception khi upload", e);
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
