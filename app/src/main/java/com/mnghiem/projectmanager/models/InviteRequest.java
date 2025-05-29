package com.mnghiem.projectmanager.models;

public class InviteRequest {
    private String email;
    private int ma_nhom;
    private int ma_nguoi_gui;

    public InviteRequest(String email, int ma_nhom, int ma_nguoi_gui) {
        this.email = email;
        this.ma_nhom = ma_nhom;
        this.ma_nguoi_gui = ma_nguoi_gui;
    }

    public String getEmail() {
        return email;
    }

    public int getMa_nhom() {
        return ma_nhom;
    }

    public int getMa_nguoi_gui() {
        return ma_nguoi_gui;
    }
}
