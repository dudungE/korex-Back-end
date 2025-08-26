package com.project.korex.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryAnswerResponseDto {
    private Long id;
    private Long inquiryId;
    private String content;
    private Long adminId;
    private LocalDateTime createdAt;
}
