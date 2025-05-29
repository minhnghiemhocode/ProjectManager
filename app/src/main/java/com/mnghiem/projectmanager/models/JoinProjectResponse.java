package com.mnghiem.projectmanager.models;

import java.util.List;

public class JoinProjectResponse {
    private boolean success;
    private List<Project> projects;

    public boolean isSuccess() {
        return success;
    }

    public List<Project> getProjects() {
        return projects;
    }
}
