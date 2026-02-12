package com.himanshu.projecttracker.controller;

import com.himanshu.projecttracker.models.TaskTrackingSystem;
import com.himanshu.projecttracker.services.TaskTrackingService;
import com.himanshu.projecttracker.utils.FileStorageUtil;
import com.himanshu.projecttracker.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@MultipartConfig
@WebServlet("/request/*")
public class MappingRequests extends HttpServlet {

    private static final String AUTH_TOKEN = "Ownerofproject3456";
    private static final String UPLOAD_DIR = "uploads"; // relative path

    private final TaskTrackingService service = new TaskTrackingService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {


        if (!("Bearer " + AUTH_TOKEN)
                .equals(request.getHeader("Authorization"))) {

            response.setStatus(401);
            response.getWriter().write("Unauthorized");
            return;
        }

        String path = normalizePath(request.getPathInfo());

        if (!"/upload".equals(path)) {
            response.setStatus(404);
            response.getWriter().write("Invalid POST endpoint");
            return;
        }

        try {

            Part filePart = request.getPart("file");

            String realUploadPath = getServletContext()
                    .getRealPath("/") + UPLOAD_DIR;

            BufferedReader reader =
                    FileStorageUtil.getJsonReaderFromPart(filePart, realUploadPath);

            TaskTrackingSystem system = JsonUtils.parseJson(reader);

            service.loadData(system);

            System.out.println("Data uploaded successfully");

            response.setStatus(200);
            response.getWriter().write("Data uploaded successfully");

        } catch (IllegalArgumentException e) {

            response.setStatus(400);
            response.getWriter().write(e.getMessage());

        } catch (Exception e) {

            response.setStatus(400);
            response.getWriter().write("Invalid JSON or validation failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String path = normalizePath(request.getPathInfo());

        if (path == null) {
            response.setStatus(400);
            response.getWriter().write("Path missing");
            return;
        }

        switch (path) {

            case "/programsByUser":
                handleProgramsByUser(request);
                break;

            case "/projectsByUser":
                handleProjectsByUser(request);
                break;

            case "/pendingTasksUser":
                handlePendingTasksUser(request);
                break;

            case "/pendingTasksUserProgram":
                handlePendingTasksUserProgram(request);
                break;

            case "/pendingTasksProject":
                handlePendingTasksProject(request);
                break;

            case "/programNameByProject":
                handleProgramNameByProject(request);
                break;

            default:
                response.setStatus(404);
                response.getWriter().write("Invalid GET endpoint");
                return;
        }

        response.setStatus(200);
        response.getWriter().write("Request processed successfully");
    }

    private void handleProgramsByUser(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        List<String> result = service.getProgramsByUser(userId);
        System.out.println("List of Programmes user "+userId +" is enrolled in : ");
        System.out.println(result);
    }

    private void handleProjectsByUser(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        List<String> result = service.getProjectsByUser(userId);
        if(result.isEmpty()){
            System.out.println("no projects enrolled in: ");
        }
        else{
            System.out.println("List of Projects user "+userId +" is enrolled in : ");
            System.out.println(result);
        }

    }

    private void handlePendingTasksUser(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        List<String> result = service.getPendingTasksForUser(userId);
        if(result.isEmpty()){
            System.out.println("no pending tasks");
        }
        else{
            System.out.println("List of Pending task for user "+userId +" is enrolled in : ");
            System.out.println(result);
        }

    }

    private void handlePendingTasksUserProgram(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String programId = request.getParameter("programId");
        List<String> result = service.getPendingTasksForUserInProgram(userId, programId);
        if(result.isEmpty()){
            System.out.println("no pending tasks");
        }
        else{
            System.out.println("List of Pending task for user "+userId +" under project "+programId+" is enrolled in : ");
            System.out.println(result);
        }

    }

    private void handlePendingTasksProject(HttpServletRequest request) {
        String projectId = request.getParameter("projectId");
        List<String> result = service.getPendingTasksForProject(projectId);
        if(result.isEmpty()){
            System.out.println("no pending tasks");
        }
        else {
            System.out.println("List of total Pending task for project "+projectId+":");

            System.out.println(result);
        }

    }

    private void handleProgramNameByProject(HttpServletRequest request) {
        String projectId = request.getParameter("projectId");
        String result = service.getProgramNameForProject(projectId);
        System.out.println("Programme corresponding to given project "+projectId+":");
        System.out.println(result);
    }

    private String normalizePath(String path) {
        if (path == null) return null;

        path = path.trim();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
