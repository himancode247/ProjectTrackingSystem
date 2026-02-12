package com.himanshu.projecttracker.utils;

import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileStorageUtil {

    public static BufferedReader getJsonReaderFromPart(Part filePart, String uploadDir)
            throws IOException {

        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException("File is missing");
        }

        if (!"application/json".equals(filePart.getContentType())) {
            throw new IllegalArgumentException("Only JSON files are allowed");
        }

        // Ensure directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = Paths.get(filePart.getSubmittedFileName())
                .getFileName()
                .toString();

        Path targetFile = uploadPath.resolve(originalFileName);

        // If file exists → rename
        if (Files.exists(targetFile)) {
            String newName = System.currentTimeMillis() + "-" + originalFileName;
            targetFile = uploadPath.resolve(newName);
        }

        // Save file
        try (InputStream in = filePart.getInputStream()) {
            Files.copy(in, targetFile);
        }

        // Return reader for parsing
        return Files.newBufferedReader(targetFile, StandardCharsets.UTF_8);
    }
}
