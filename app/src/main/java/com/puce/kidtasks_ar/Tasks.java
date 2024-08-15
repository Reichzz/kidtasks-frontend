package com.puce.kidtasks_ar;

public class Tasks {
    String id;
    String description;
    Boolean completed;
    String child;

    //id get-set
    public String getIdTasks() {
        return id;
    }
    public void setIdTasks(String id) {
        this.id = id;
    }

    //description get-set
    public String getDescriptionTasks() {
        return description;
    }
    public void setDescriptionTasks(String description) {
        this.description = description;
    }

    //completed get-set
    public Boolean getCompletedTasks() {
        return completed;
    }
    public void setCompletedTasks(Boolean completed) {
        this.completed = completed;
    }

    //completed get-set
    public String getChildTasks() {
        return child;
    }
    public void setChildTasks(String child) {
        this.child = child;
    }

}

