package com.project.korex.ForeignTransfer.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileUploadResponse {

    private Long id;                      // DB 저장 후 생성된 ID
    private String fileType;              // 문서 종류
    private String originalFilename;      // 원본 파일 이름
    private String storedFilename;        // 서버에 저장된 파일 이름
    private String fileUrl;               // 접근 가능한 URL
    private long fileSize;                 // 파일 크기 (byte)
    private String contentType;           // MIME 타입
    private LocalDateTime uploadedAt;       // 업로드 시각
}
