package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.entity.FileUpload;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final String uploadDir = "uploads/"; // 서버 내 저장 경로

    // 단순 파일 업로드 (서버 저장 파일명 반환)
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) return null;

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID() + extension;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(storedFilename);
            file.transferTo(filePath.toFile());

            return storedFilename; // 서버 저장용 파일명 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    // ForeignTransferTransaction에 연결 후 FileUpload 저장
    public FileUpload uploadFileToTransaction(ForeignTransferTransaction transaction, MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) return null;

        String storedFilename = uploadFile(file); // 실제 저장 파일명
        String url = "/uploads/" + storedFilename;  // 웹 접근용 URL

        FileUpload fu = new FileUpload();
        fu.setForeignTransferTransaction(transaction);
        fu.setOriginalFilename(file.getOriginalFilename());
        fu.setStoredFilename(storedFilename); // 서버에 실제 저장된 파일명 사용
        fu.setFileUrl(url);
        fu.setFileType(fileType);
        fu.setFileSize(file.getSize());
        fu.setContentType(file.getContentType());
        fu.setUploadedAt(LocalDateTime.now());

        return fileUploadRepository.save(fu);
    }
}
