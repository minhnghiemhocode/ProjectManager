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

    @SerializedName("gioi_tinh")
    private String gioiTinh;

    public User(int maNd, String hoTen, String email, String anhDaiDien, String gioiTinh) {
        this.maNd = maNd;
        this.hoTen = hoTen;
        this.email = email;
        this.anhDaiDien = anhDaiDien;
        this.gioiTinh = gioiTinh;
    }

    public User() {
        // Constructor rỗng để sử dụng khi chỉ muốn set một vài thuộc tính
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

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
}
