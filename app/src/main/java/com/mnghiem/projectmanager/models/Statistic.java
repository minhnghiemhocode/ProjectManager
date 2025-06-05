package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Statistic {

    @SerializedName("tong")
    private int tong;

    @SerializedName("hoan_thanh")
    private int hoan_thanh;

    @SerializedName("tre_han")
    private int tre_han;

    @SerializedName("ho_ten")
    private String ho_ten;

    @SerializedName("ten_nhom")
    private String ten_nhom;

    @SerializedName("label")  // dùng cho line chart
    private String label;

    public int getTong() {
        return tong;
    }

    public int getHoan_thanh() {
        return hoan_thanh;
    }

    public int getTre_han() {
        return tre_han;
    }

    public String getHo_ten() {
        return ho_ten;
    }

    public String getTen_nhom() {
        return ten_nhom;
    }

    public String getLabel() {
        return label;
    }
}
