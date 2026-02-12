package com.himanshu.projecttracker.services;

import com.himanshu.projecttracker.models.*;

import java.util.*;

public class TaskTrackingService {

    private TaskTrackingSystem system;

    public void loadData(TaskTrackingSystem system) {
        validate(system);
        this.system = system;
    }

    private void validate(TaskTrackingSystem system) {

        if (system == null) {
            throw new RuntimeException("System data is null");
        }

        if (system.getIndividuals() == null || system.getIndividuals().isEmpty()) {
            throw new RuntimeException("Individuals list is missing or empty");
        }

        if (system.getPrograms() == null || system.getPrograms().isEmpty()) {
            throw new RuntimeException("Programs list is missing or empty");
        }


        Set<String> individualIds = new HashSet<>();

        for (Individual individual : system.getIndividuals()) {

            String userId = normalize(individual.getUserId());
            String name = individual.getName();
            if (userId == null || userId.isEmpty()) {
                throw new RuntimeException("Individual ID missing");
            }

            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Individual name missing for ID: " + userId);
            }

            if (!individualIds.add(userId)) {
                throw new RuntimeException("Duplicate Individual ID: " + userId);
            }
        }

        Set<String> programIds = new HashSet<>();
        Set<String> projectIds = new HashSet<>();
        Set<String> taskIds = new HashSet<>();

        for (Program program : system.getPrograms()) {

            String programId = normalize(program.getProgramId());
            String programName = program.getProgramName();

            if (programId == null || programId.isEmpty()) {
                throw new RuntimeException("Program ID missing");
            }

            if (programName == null || programName.trim().isEmpty()) {
                throw new RuntimeException("Program name missing for ID: " + programId);
            }

            if (!programIds.add(programId)) {
                throw new RuntimeException("Duplicate Program ID: " + programId);
            }

            if (program.getProjects() == null || program.getProjects().isEmpty()) {
                throw new RuntimeException("Program must contain at least one project: " + programId);
            }

            for (Project project : program.getProjects()) {

                String projectId = normalize(project.getProjectId());
                String projectName = project.getProjectName();

                if (projectId == null || projectId.isEmpty()) {
                    throw new RuntimeException("Project ID missing");
                }

                if (projectName == null || projectName.trim().isEmpty()) {
                    throw new RuntimeException("Project name missing for ID: " + projectId);
                }

                if (!projectIds.add(projectId)) {
                    throw new RuntimeException("Duplicate Project ID: " + projectId);
                }

                if (project.getEnrolledIndividuals() == null ||
                        project.getEnrolledIndividuals().isEmpty()) {
                    throw new RuntimeException("Project must have enrolled individuals: " + projectId);
                }


                Set<String> projectUserSet = new HashSet<>();
                for (String userIdRaw : project.getEnrolledIndividuals()) {

                    String userId = normalize(userIdRaw);

                    if (!individualIds.contains(userId)) {
                        throw new RuntimeException(
                                "Project enrolled individual does not exist: " + userId);
                    }

                    if (!projectUserSet.add(userId)) {
                        throw new RuntimeException(
                                "Duplicate enrolled individual in project: "
                                        + userId + ", Project: " + projectId);
                    }
                }

                if (project.getTasks() == null || project.getTasks().isEmpty()) {
                    throw new RuntimeException("Project must contain at least one task: " + projectId);
                }

                for (Task task : project.getTasks()) {

                    String taskId = normalize(task.getTaskId());
                    String taskName = task.getTaskName();
                    String owner = normalize(task.getOwner());

                    if (taskId == null || taskId.isEmpty()) {
                        throw new RuntimeException("Task ID missing");
                    }

                    if (taskName == null || taskName.trim().isEmpty()) {
                        throw new RuntimeException("Task name missing for ID: " + taskId);
                    }

                    if (!taskIds.add(taskId)) {
                        throw new RuntimeException("Duplicate Task ID: " + taskId);
                    }


                    if (owner == null || owner.isEmpty()) {
                        throw new RuntimeException("Task owner missing in task: " + taskId);
                    }

                    if (!individualIds.contains(owner)) {
                        throw new RuntimeException(
                                "Task owner does not exist: " + owner);
                    }

                    if (!projectUserSet.contains(owner)) {
                        throw new RuntimeException(
                                "Task owner not enrolled in project. Owner: "
                                        + owner + ", Project: " + projectId);
                    }

                    if (task.getEnrolledIndividuals() == null ||
                            task.getEnrolledIndividuals().isEmpty()) {
                        throw new RuntimeException(
                                "Task must have enrolled individuals: " + taskId);
                    }


                    Set<String> taskUserSet = new HashSet<>();

                    for (String userIdRaw : task.getEnrolledIndividuals()) {

                        String userId = normalize(userIdRaw);

                        if (!individualIds.contains(userId)) {
                            throw new RuntimeException(
                                    "Task participant does not exist: " + userId);
                        }

                        if (!projectUserSet.contains(userId)) {
                            throw new RuntimeException(
                                    "Task participant not enrolled in project. User: "
                                            + userId + ", Project: " + projectId);
                        }

                        if (!taskUserSet.add(userId)) {
                            throw new RuntimeException(
                                    "Duplicate participant in task: "
                                            + userId + ", Task: " + taskId);
                        }
                    }

                    if (!taskUserSet.contains(owner)) {
                        throw new RuntimeException(
                                "Task owner not present in task enrolledIndividuals. Task: " + taskId);
                    }

                    if (task.getStatus() == null) {
                        throw new RuntimeException(
                                "Invalid task status in task: " + taskId);
                    }
                }
            }
        }
    }
    private String normalize(String value) {
        if (value == null) return null;
        return value.trim().toUpperCase();
    }




    public List<String> getProgramsByUser(String userId) {

        List<String> result = new ArrayList<>();

        for (Program program : system.getPrograms()) {

            for (Project project : program.getProjects()) {

                if (project.getEnrolledIndividuals().contains(userId)) {
                    result.add(program.getProgramName());
                    break;
                }
            }
        }

        return result;
    }

    public List<String> getProjectsByUser(String userId) {

        List<String> result = new ArrayList<>();

        for (Program program : system.getPrograms()) {

            for (Project project : program.getProjects()) {

                if (project.getEnrolledIndividuals().contains(userId)) {
                    result.add(project.getProjectName());
                }
            }
        }

        return result;
    }

    public List<String> getPendingTasksForUser(String userId) {

        List<String> result = new ArrayList<>();

        for (Program program : system.getPrograms()) {

            for (Project project : program.getProjects()) {

                for (Task task : project.getTasks()) {

                    if (task.getStatus() == Status.PENDING) {

                        boolean isEnrolled =
                                task.getEnrolledIndividuals() != null &&
                                        task.getEnrolledIndividuals().contains(userId);

                        if (isEnrolled) {
                            result.add(task.getTaskName());
                        }
                    }
                }
            }
        }

        return result;
    }


    public List<String> getPendingTasksForUserInProgram(String userId, String programId) {

        int count = 0;
        List<String> result = new ArrayList<>();

        for (Program program : system.getPrograms()) {

            if (program.getProgramId().equals(programId)) {

                for (Project project : program.getProjects()) {

                    for (Task task : project.getTasks()) {
                        boolean isEnrolled = task.getEnrolledIndividuals() != null &&
                                        task.getEnrolledIndividuals().contains(userId);

                        if (isEnrolled
                                && task.getStatus() == Status.PENDING) {
                            result.add(task.getTaskName());
                        }
                    }
                }
            }
        }

        return result;
    }

    public List<String> getPendingTasksForProject(String projectId) {

        int count = 0;
        List<String> result = new ArrayList<>();

        for (Program program : system.getPrograms()) {

            for (Project project : program.getProjects()) {

                if (project.getProjectId().equals(projectId)) {

                    for (Task task : project.getTasks()) {

                        if (task.getStatus() == Status.PENDING) {
                            result.add(task.getTaskName());
                        }
                    }
                }
            }
        }

        return result;
    }

    public String getProgramNameForProject(String projectId) {

        for (Program program : system.getPrograms()) {

            for (Project project : program.getProjects()) {

                if (project.getProjectId().equals(projectId)) {
                    return program.getProgramName();
                }
            }
        }

        return "Not Found";
    }
}
