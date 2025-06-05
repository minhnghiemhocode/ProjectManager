package com.mnghiem.projectmanager.ai;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.*;

public class OpenRouterClient {

    private static final String TAG = "OpenRouterClient";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "openai/gpt-3.5-turbo";

    private static final OkHttpClient client = new OkHttpClient();
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public interface AIListener {
        void onResult(String message);
        void onError(String error);
    }

    public static void askSuggestion(String prompt, String apiKey, AIListener listener) {
        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("model", MODEL);

                JSONArray messages = new JSONArray();
                JSONObject sys = new JSONObject();
                sys.put("role", "system");
                sys.put("content", "Bạn là một trợ lý chuyên đưa ra lời khuyên về quản lý thời gian.");
                messages.put(sys);

                JSONObject user = new JSONObject();
                user.put("role", "user");
                user.put("content", prompt);
                messages.put(user);

                body.put("messages", messages);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("HTTP-Referer", "https://yourapp.com") // hoặc để ""
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    listener.onError("Lỗi HTTP: " + response.code());
                    return;
                }

                String json = response.body().string();
                JSONObject obj = new JSONObject(json);
                String reply = obj
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                listener.onResult(reply);

            } catch (IOException | JSONException e) {
                listener.onError("❌ Lỗi AI: " + e.getMessage());
                Log.e(TAG, "❌ Lỗi AI: ", e);
            }
        });
    }
}
