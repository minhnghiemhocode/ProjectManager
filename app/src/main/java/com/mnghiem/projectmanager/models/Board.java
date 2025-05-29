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
    private boolean pinned;

    @SerializedName("so_task")
    private int soTask;

    // === Constructor mặc định ===
    public Board() {}

    // === Getter ===
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

    public boolean isPinned() {
        return pinned;
    }

    public int getSoTask() {
        return soTask;
    }

    // === Setter ===
    public void setBoardId(int boardId) {
        this.boardId = boardId;
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

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setSoTask(int soTask) {
        this.soTask = soTask;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
