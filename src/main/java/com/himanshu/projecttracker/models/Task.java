package com.himanshu.projecttracker.models;
import java.util.List;
public class Task {

    private String taskId;
    private String taskName;
    private String owner;   // must match Individual.userId
    private Status status;
    private List<String> enrolledIndividuals;

    public List<String> getEnrolledIndividuals() {
        return enrolledIndividuals;
    }
    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getOwner() {
        return owner;
    }

    public Status getStatus() {
        return status;
    }
}
