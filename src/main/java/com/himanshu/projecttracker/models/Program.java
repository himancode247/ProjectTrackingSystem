package com.himanshu.projecttracker.models;

import java.util.List;

public class Program {

    private String programId;
    private String programName;
    private List<Project> projects;

    public Program() {}

    public String getProgramId() {
        return programId;
    }

    public String getProgramName() {
        return programName;
    }

    public List<Project> getProjects() {
        return projects;
    }
}
