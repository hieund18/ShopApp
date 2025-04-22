package com.project.shopapp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${upload.path}")
    private String uploadPath;

    public String storeFile(MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().startsWith("image/"))
            throw new AppException(ErrorCode.INVALID_FILE);

        if (file.getSize() > 10 * 1024 * 1024) throw new AppException(ErrorCode.INVALID_FILE_SIZE);

        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadPath, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        Path filePath = Paths.get(uploadPath, fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
