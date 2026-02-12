package com.himanshu.projecttracker.utils;

import com.google.gson.Gson;
import com.himanshu.projecttracker.models.TaskTrackingSystem;

import java.io.BufferedReader;

public class JsonUtils {

    public static TaskTrackingSystem parseJson(BufferedReader reader) {

        Gson gson = new Gson();
        return gson.fromJson(reader, TaskTrackingSystem.class);
    }
}
