package com.project.korex.ForeignTransfer.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SenderRequest {
    // 기본 정보
    private String name;                  // 송금인 이름
    private String transferReason;        // 송금 사유
    private String countryNumber;         // 국가 번호
    private String phoneNumber;           // 연락처
    private String email;                 // 이메일
    private String country;               // 거주 국가
    private String engAddress;            // 영문 주소
    private String staffMessage;          // 담당자 메세지
    private String relationRecipient;     // 수취인과의 관계

    // 파일 업로드 (multipart/form-data 로 전송)
    private MultipartFile idFile;             // 신분증 사본
    private MultipartFile proofDocumentFile;  // 송금 증빙 서류
    private MultipartFile relationDocumentFile; // 관계 증빙 서류
}
