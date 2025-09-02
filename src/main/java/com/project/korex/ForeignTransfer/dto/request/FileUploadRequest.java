package com.project.korex.ForeignTransfer.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileUploadRequest {

    @NotNull
    private MultipartFile file;        // 업로드할 파일

    @NotNull
    private String fileType;            // 문서 종류: ID, PROOF, RELATION 등

    @NotNull
    private Long transferId;            // 어느 ForeignTransferTransaction과 연결할지
}