package com.college.resource.controller;

import com.college.resource.util.FileStorageUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageUtil fileStorageUtil;

    public FileController(FileStorageUtil fileStorageUtil) {
        this.fileStorageUtil = fileStorageUtil;
    }

    @GetMapping("/view/**")
    public ResponseEntity<Resource> viewFile(jakarta.servlet.http.HttpServletRequest request) {
        try {
            String fullPath = request.getRequestURI();
            String prefix = "/api/files/view/";
            int idx = fullPath.indexOf(prefix);
            String storagePath = idx >= 0 ? fullPath.substring(idx + prefix.length()) : "";

            Path filePath = fileStorageUtil.resolvePath(storagePath);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                String name = filePath.getFileName().toString().toLowerCase();
                if (name.endsWith(".jpg") || name.endsWith(".jpeg")) contentType = "image/jpeg";
                else if (name.endsWith(".png")) contentType = "image/png";
                else if (name.endsWith(".webp")) contentType = "image/webp";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
