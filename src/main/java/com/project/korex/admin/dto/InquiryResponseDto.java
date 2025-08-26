package com.project.korex.admin.dto;

import com.project.korex.support.enums.InquiryCategory;
import com.project.korex.support.enums.InquiryStatus;
import com.project.korex.user.entity.Users;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryResponseDto {

    private Long id;
    private Long userId;
    private String userName;
    private String title;
    private InquiryCategory category;
    private InquiryStatus status;
    private LocalDateTime createdAt;
    private String content;

}
