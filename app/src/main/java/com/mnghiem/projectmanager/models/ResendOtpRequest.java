package com.mnghiem.projectmanager.models;

public class ResendOtpRequest {
    private String email;

    public ResendOtpRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
