package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Attachment {

    @SerializedName("ma_tep")
    private int maTep;

    @SerializedName("ma_cv")
    private int maCongViec;

    @SerializedName("duong_dan")
    private String duongDan;

    @SerializedName("tai_len_boi")
    private int taiLenBoi;

    public int getMaTep() {
        return maTep;
    }

    public int getMaCongViec() {
        return maCongViec;
    }

    public String getDuongDan() {
        return duongDan;
    }

    public int getTaiLenBoi() {
        return taiLenBoi;
    }
}
