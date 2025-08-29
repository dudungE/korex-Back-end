package com.project.korex.ForeignTransfer.entity;

import com.project.korex.transaction.entity.Transaction;
import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ForeignTransferTransaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ForeignTransferTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @OneToOne
    @JoinColumn(name = "recipient_id")
    private Recipient recipient;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToOne(mappedBy = "foreignTransferTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Sender sender;

    @OneToOne(mappedBy = "foreignTransferTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private TermsAgreement termsAgreement;

    @OneToMany(mappedBy = "foreignTransferTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileUpload> fileUploads = new ArrayList<>();

    @Column(name = "fee_amount", precision = 18, scale = 4)
    private BigDecimal feeAmount;

    @Column(name = "relation_recipient")
    private String relationRecipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status")
    private TransferStatus transferStatus;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "estimated_arrival_date")
    private LocalDateTime estimatedArrivalDate; // 서비스에서 세팅

    @Column(name = "actual_arrival_date")
    private LocalDateTime actualArrivalDate; // 서비스에서 세팅

    @Column(name = "transfer_amount")
    private BigDecimal transferAmount;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "krw_number")
    private String krwNumber;

    @Column(name = "foreign_number")
    private String foreignNumber;

    @Column(name = "account_password")
    private String accountPassword;

    @Column(name = "staff_message")
    private String staffMessage;

    // 편의 메서드: Sender와 양방향 관계 설정
    public void setSender(Sender sender) {
        this.sender = sender;
        if (sender.getForeignTransferTransaction() != this) {
            sender.setForeignTransferTransaction(this);
        }
    }

    // 상태 Enum
    public enum RequestStatus {
        NOT_STARTED, // 송금 요청 전
        PENDING, // 송금 요청 생성, 약관 동의 대기
        APPROVED, // 약관 동의 및 해외송금 트랜잭션 생성
        SUBMITTED // 송금 요청 완료
    }

    // 상태 Enum
    public enum TransferStatus {
        NOT_STARTED, // 송금 진행 전
        IN_PROGRESS, // 송금 처리 중
        COMPLETED, // 송금 성공
        FAILED // 송금 실패
    }
}