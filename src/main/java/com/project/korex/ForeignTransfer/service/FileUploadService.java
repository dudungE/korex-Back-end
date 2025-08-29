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

    // 단순 파일 업로드 (경로 반환)
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

            return "/uploads/" + storedFilename; // 웹 접근용 URL
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    // 트랜잭션과 연결해서 FileUpload 엔티티 저장
    public FileUpload uploadFileToTransaction(ForeignTransferTransaction transaction, MultipartFile file, String fileType) {
        String url = uploadFile(file);

        FileUpload fu = new FileUpload();
        fu.setForeignTransferTransaction(transaction);
        fu.setOriginalFilename(file.getOriginalFilename());
        fu.setStoredFilename(UUID.randomUUID() + file.getOriginalFilename());
        fu.setFileUrl(url);
        fu.setFileType(fileType);
        fu.setFileSize((int) file.getSize());
        fu.setContentType(file.getContentType());
        fu.setUploadedAt(LocalDateTime.now());

        return fileUploadRepository.save(fu);
    }
}
