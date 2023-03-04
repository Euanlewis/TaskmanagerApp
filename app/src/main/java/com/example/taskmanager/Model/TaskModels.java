package com.example.taskmanager.Model;

// Creates getters and setting for tasks

public class TaskModels {
    private int id, status, priorityRating;
    private String task;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriorityRating() {
        return priorityRating;
    }

    public void setPriorityRating(int priorityRating) {
        this.priorityRating = priorityRating;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
