package com.project.korex.ForeignTransfer.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SenderRequest {
    private String name;
    private String transferReason;
    private String countryNumber;
    private String phoneNumber;
    private String email;
    private String country;
    private String engAddress;
    private String staffMessage;
    private String relationRecipient;

    // MultipartFile로 업로드 받기
    private MultipartFile idFile;
    private MultipartFile proofDocumentFile;
    private MultipartFile relationDocumentFile;
}
