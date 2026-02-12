package com.himanshu.projecttracker.models;

import java.util.List;

public class Project {

    private String projectId;
    private String projectName;
    private List<String> enrolledIndividuals;
    private List<Task> tasks;

    public Project() {}

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<String> getEnrolledIndividuals() {
        return enrolledIndividuals;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
