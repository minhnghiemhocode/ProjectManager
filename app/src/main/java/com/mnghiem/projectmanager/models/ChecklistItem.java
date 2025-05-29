package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class ChecklistItem {

    @SerializedName("ma_item")
    private int maItem;

    @SerializedName("ma_cv")
    private int maCongViec;

    @SerializedName("noi_dung")
    private String noiDung;

    @SerializedName("da_hoan_thanh")
    private boolean daHoanThanh;

    public ChecklistItem(int maCongViec, String noiDung) {
        this.maCongViec = maCongViec;
        this.noiDung = noiDung;
        this.daHoanThanh = false;
    }

    public int getMaItem() {
        return maItem;
    }

    public int getMaCongViec() {
        return maCongViec;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public boolean isDaHoanThanh() {
        return daHoanThanh;
    }

    public void setDaHoanThanh(boolean daHoanThanh) {
        this.daHoanThanh = daHoanThanh;
    }
}
