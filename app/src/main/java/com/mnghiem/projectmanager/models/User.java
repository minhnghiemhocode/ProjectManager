package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("ma_nd")
    private int maNd;

    @SerializedName("ho_ten")
    private String hoTen;

    @SerializedName("email")
    private String email;

    @SerializedName("anh_dai_dien")
    private String anhDaiDien;

    public User(int maNd, String hoTen, String email, String anhDaiDien) {
        this.maNd = maNd;
        this.hoTen = hoTen;
        this.email = email;
        this.anhDaiDien = anhDaiDien;
    }

    public int getMaNd() {
        return maNd;
    }

    public void setMaNd(int maNd) {
        this.maNd = maNd;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }
}
