package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("id")
    private int ma_cv;

    @SerializedName("title")
    private String tieu_de;

    @SerializedName("status")
    private String trang_thai;

    @SerializedName("deadline")
    private String han_hoan_thanh;

    @SerializedName("listName")
    private String ten_thu_muc;

    @SerializedName("projectName")
    private String ten_nhom;

    @SerializedName("nguoiGiao")
    private String nguoiGiao;

    @SerializedName("assigneeId")
    private Integer maNguoiGiao;

    @SerializedName("mo_ta")
    private String mo_ta;

    @SerializedName("do_uu_tien")
    private String do_uu_tien;

    @SerializedName("ma_nhom")
    private int ma_nhom;

    @SerializedName("ma_thu_muc")
    private int ma_thu_muc;

    @SerializedName("tao_boi")
    private int tao_boi;

    @SerializedName("assigneeAvatarUrl")
    private String assigneeAvatarUrl;

    public Task() {}

    public int getMa_cv() {
        return ma_cv;
    }

    public void setMa_cv(int ma_cv) {
        this.ma_cv = ma_cv;
    }

    public String getTieu_de() {
        return tieu_de;
    }

    public void setTieu_de(String tieu_de) {
        this.tieu_de = tieu_de;
    }

    public String getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }

    public String getHan_hoan_thanh() {
        return han_hoan_thanh;
    }

    public void setHan_hoan_thanh(String han_hoan_thanh) {
        this.han_hoan_thanh = han_hoan_thanh;
    }

    public String getTen_thu_muc() {
        return ten_thu_muc;
    }

    public void setTen_thu_muc(String ten_thu_muc) {
        this.ten_thu_muc = ten_thu_muc;
    }

    public String getTen_nhom() {
        return ten_nhom;
    }

    public void setTen_nhom(String ten_nhom) {
        this.ten_nhom = ten_nhom;
    }

    public String getNguoiGiao() {
        return nguoiGiao;
    }

    public void setNguoiGiao(String nguoiGiao) {
        this.nguoiGiao = nguoiGiao;
    }

    public Integer getMaNguoiGiao() {
        return maNguoiGiao;
    }

    public void setMaNguoiGiao(Integer maNguoiGiao) {
        this.maNguoiGiao = maNguoiGiao;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }

    public String getDo_uu_tien() {
        return do_uu_tien;
    }

    public void setDo_uu_tien(String do_uu_tien) {
        this.do_uu_tien = do_uu_tien;
    }

    public int getMa_nhom() {
        return ma_nhom;
    }

    public void setMa_nhom(int ma_nhom) {
        this.ma_nhom = ma_nhom;
    }

    public int getMa_thu_muc() {
        return ma_thu_muc;
    }

    public void setMa_thu_muc(int ma_thu_muc) {
        this.ma_thu_muc = ma_thu_muc;
    }

    public int getTao_boi() {
        return tao_boi;
    }

    public void setTao_boi(int tao_boi) {
        this.tao_boi = tao_boi;
    }

    public String getAssigneeAvatarUrl() {
        return assigneeAvatarUrl;
    }

    public void setAssigneeAvatarUrl(String assigneeAvatarUrl) {
        this.assigneeAvatarUrl = assigneeAvatarUrl;
    }
}
