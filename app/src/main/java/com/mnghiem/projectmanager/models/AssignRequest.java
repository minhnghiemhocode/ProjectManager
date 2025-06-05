package com.mnghiem.projectmanager.models;

import com.google.gson.annotations.SerializedName;

public class AssignRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("taskId")
    private int taskId;

    @SerializedName("groupId")
    private int assignerId;

    public AssignRequest(String email, int taskId, int assignerId) {
        this.email = email;
        this.taskId = taskId;
        this.assignerId = assignerId;
    }

    public String getEmail() {
        return email;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getAssignerId() {
        return assignerId;
    }
}
