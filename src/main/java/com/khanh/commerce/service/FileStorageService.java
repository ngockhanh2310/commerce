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

    // NOTE: thư mục uploads → tuyệt đối + normalize
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            // NOTE: Tạo thư mục nếu chưa tồn tại
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create upload folder.", ex);
        }
    }

    public String storeFile(MultipartFile file) {

        // NOTE: 1. Kiểm tra file rỗng
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        // NOTE: 2. Giới hạn file size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileStorageException("File size exceeds limit (5MB)");
        }

        // NOTE: 3. Chỉ cho phép image/*
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileStorageException("Only image files are allowed.");
        }

        try {
            // NOTE: 4. Lấy extension (.jpg, .png,…)
            String ext = extractFileExtension(file);

            // NOTE: 5. Tạo tên mới
            String newFileName = UUID.randomUUID() + ext;

            // NOTE: 6. Tạo đường dẫn save file
            Path target = this.fileStorageLocation.resolve(newFileName);

            // NOTE: 7. Copy file
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // NOTE: 8. Trả về tên file để lưu DB
            return newFileName;

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file", ex);
        }
    }

    // NOTE: Method tách extension — Đặt tên rõ ràng hơn
    private String extractFileExtension(MultipartFile file) {

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new FileStorageException("Invalid file name");
        }

        // NOTE: Làm sạch tên file để tránh tấn công path traversal
        originalFileName = Path.of(originalFileName).getFileName().toString();

        // NOTE: Lấy extension
        int idx = originalFileName.lastIndexOf(".");
        if (idx >= 0) {
            return originalFileName.substring(idx);
        }

        return ""; // Trường hợp không có extension
    }
}
