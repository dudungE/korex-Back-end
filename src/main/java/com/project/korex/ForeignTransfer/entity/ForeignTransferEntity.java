package com.project.korex.ForeignTransfer.entity;

import jakarta.persistence.Column;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public class ForeignTransferEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime EstimatedArrivalDate; // 해외송금 예상 도착일

    @LastModifiedDate
    private LocalDateTime ActualArrivalDate; // 해외송금 실제 도착일

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime AgreedAt; // 동의 날짜
}
