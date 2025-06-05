package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class ProjectStats {
    @SerializedName("ma_nhom")
    private int maNhom;

    @SerializedName("ten_nhom")
    private String tenNhom;

    @SerializedName("so_board")
    private int soBoard;

    @SerializedName("so_task")
    private int soTask;

    public int getMaNhom() {
        return maNhom;
    }

    public String getTenNhom() {
        return tenNhom;
    }

    public int getSoBoard() {
        return soBoard;
    }

    public int getSoTask() {
        return soTask;
    }


}
