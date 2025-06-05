package com.mnghiem.projectmanager.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.cloudinary.Cloudinary;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CloudinaryHelper {

    private static Cloudinary cloudinary;

    public static Cloudinary getInstance(Context context) {
        if (cloudinary == null) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("cloudinary_config.properties");

                Properties props = new Properties();
                props.load(inputStream);

                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", props.getProperty("cloud_name"));
                config.put("api_key", props.getProperty("api_key"));
                config.put("api_secret", props.getProperty("api_secret"));

                cloudinary = new Cloudinary(config);
            } catch (Exception e) {
                Log.e("CloudinaryHelper", "Lỗi khi đọc file cấu hình", e);
            }
        }
        return cloudinary;
    }
}
