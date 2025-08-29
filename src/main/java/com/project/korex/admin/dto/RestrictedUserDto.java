package com.project.korex.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestrictedUserDto {
    private Long id;
    private String email;
    private String reason;
    private LocalDateTime restrictedAt;
}