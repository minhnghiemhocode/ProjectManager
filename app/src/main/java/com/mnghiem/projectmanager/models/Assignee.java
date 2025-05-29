package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Assignee {

    @SerializedName("ma_giao")
    private int maGiao;

    @SerializedName("ma_cv")
    private int maCongViec;

    @SerializedName("ma_nd")
    private int maNguoiDung;

    @SerializedName("ngay_giao")
    private String ngayGiao;

    public Assignee(int maCongViec, int maNguoiDung) {
        this.maCongViec = maCongViec;
        this.maNguoiDung = maNguoiDung;
    }

    public int getMaGiao() {
        return maGiao;
    }

    public int getMaCongViec() {
        return maCongViec;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public String getNgayGiao() {
        return ngayGiao;
    }
}
