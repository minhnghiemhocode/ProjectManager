package com.mnghiem.projectmanager.models;

public class OverviewStats {
    private String tong;
    private String hoan_thanh;
    private String tre_han;

    public String getTong() {
        return tong;
    }

    public String getHoan_thanh() {
        return hoan_thanh;
    }

    public String getTre_han() {
        return tre_han;
    }

    @Override
    public String toString() {
        return "Tổng=" + tong + ", Hoàn thành=" + hoan_thanh + ", Trễ hạn=" + tre_han;
    }
}
