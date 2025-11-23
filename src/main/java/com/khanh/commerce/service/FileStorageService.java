package com.khanh.commerce.service;

import com.khanh.commerce.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            // Tạo thư mục nếu chưa tồn tại
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create upload folder.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        //  file size max (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileStorageException("File size exceeds limit (5MB)");
        }

        // image/*
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileStorageException("Only image files are allowed.");
        }
        
        try {
            String ext = extractFileExtension(file);

            String newFileName = UUID.randomUUID() + ext;

            Path target = this.fileStorageLocation.resolve(newFileName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file", ex);
        }
    }

    private String extractFileExtension(MultipartFile file) {

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new FileStorageException("Invalid file name");
        }

        originalFileName = Path.of(originalFileName).getFileName().toString();

        // NOTE: Lấy extension
        int idx = originalFileName.lastIndexOf(".");
        if (idx >= 0) {
            return originalFileName.substring(idx);
        }

        return "";
    }
}
