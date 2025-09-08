package com.project.korex.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InquiryAnswerRequestDto {
    private Long inquiryId;
    private String content;
}
