package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Attachment {
    @SerializedName("ma_tep")
    private int maTep;

    @SerializedName("ma_cv")
    private int maCv;

    @SerializedName("duong_dan")
    private String duongDan;

    @SerializedName("tai_len_boi")
    private int taiLenBoi;

    @SerializedName("ngay_tai_len")
    private String ngayTaiLen;

    public int getMaTep() {
        return maTep;
    }

    public void setMaTep(int maTep) {
        this.maTep = maTep;
    }

    public int getMaCv() {
        return maCv;
    }

    public void setMaCv(int maCv) {
        this.maCv = maCv;
    }

    public String getDuongDan() {
        return duongDan;
    }

    public void setDuongDan(String duongDan) {
        this.duongDan = duongDan;
    }

    public int getTaiLenBoi() {
        return taiLenBoi;
    }

    public void setTaiLenBoi(int taiLenBoi) {
        this.taiLenBoi = taiLenBoi;
    }

    public String getNgayTaiLen() {
        return ngayTaiLen;
    }

    public void setNgayTaiLen(String ngayTaiLen) {
        this.ngayTaiLen = ngayTaiLen;
    }
}
