package com.mnghiem.projectmanager.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private User user; // ✅ THÊM

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; } // ✅ THÊM
}
