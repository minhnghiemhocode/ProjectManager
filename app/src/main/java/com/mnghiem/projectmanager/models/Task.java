package com.mnghiem.projectmanager.models;

public class Task {
    private int id;
    private String title;
    private String status;
    private String deadline;
    private String listName;
    private String projectName;
    private String priority;

    public Task(int id, String title, String status, String deadline, String listName, String projectName) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.deadline = deadline;
        this.listName = listName;
        this.projectName = projectName;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getListName() {
        return listName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
