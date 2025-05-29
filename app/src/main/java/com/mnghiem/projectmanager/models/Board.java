package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class Board {

    @SerializedName("ma_thu_muc")
    private int boardId;

    @SerializedName("ten_thu_muc")
    private String title;

    @SerializedName("mo_ta")
    private String description;

    @SerializedName("ma_nhom")
    private int groupId;

    @SerializedName("ngay_tao")
    private String createdAt;

    @SerializedName("mau_nen")
    private String backgroundColor;

    @SerializedName("is_pinned")
    private boolean pinned; // ✅ mới thêm

    public int getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getBackgroundColor() {
        return backgroundColor != null ? backgroundColor : "#64B5F6";
    }

    public void setBackgroundColor(String color) {
        this.backgroundColor = color;
    }

    public boolean isPinned() { // ✅ getter mới
        return pinned;
    }

    public void setPinned(boolean pinned) { // (tuỳ chọn) setter nếu cần
        this.pinned = pinned;
    }

    @SerializedName("so_task")
    private int soTask;

    public int getSoTask() {
        return soTask;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

}
