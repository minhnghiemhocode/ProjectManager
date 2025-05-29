package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Project {

    @SerializedName("ma_nhom")
    private int maNhom;

    @SerializedName("ten_nhom")
    private String tenNhom;

    @SerializedName("mo_ta")
    private String moTa;

    @SerializedName("ngay_tao")
    private String ngayTao;

    @SerializedName("so_board")
    private int soBoard;

    @SerializedName("mau_nen")
    private String mauNen;

    @SerializedName("tao_boi")
    private int maNguoiTao;

    // Constructor dùng khi tạo mới
    public Project(String tenNhom, String moTa) {
        this.tenNhom = tenNhom;
        this.moTa = moTa;
    }

    // Getters
    public int getMaNhom() {
        return maNhom;
    }

    public String getTenNhom() {
        return tenNhom;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public int getSoBoard() {
        return soBoard;
    }

    public String getMauNen() {
        return mauNen != null ? mauNen : "#FFF8E1"; // fallback màu mặc định
    }

    // Optional setter nếu bạn cần thay đổi màu trong app
    public void setMauNen(String mauNen) {
        this.mauNen = mauNen;
    }

    public int getMaNguoiTao() {
        return maNguoiTao;
    }
}
