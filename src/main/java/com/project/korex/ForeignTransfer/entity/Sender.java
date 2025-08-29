package com.project.korex.ForeignTransfer.entity;

import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ForeignTransferSender")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sender_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "transfer_id")
    private ForeignTransferTransaction foreignTransferTransaction;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "name")
    private String name;

    @Column(name = "transfer_reason")
    private String transferReason;

    @Column(name = "country_number")
    private String countryNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "country")
    private String country;

    @Column(name = "eng_address")
    private String engAddress;

    @Column(name = "staff_message")
    private String staffMessage;

    @Column(name = "relation_recipient")
    private String relationRecipient;

    @Column(name = "id_file_path")
    private String idFilePath;

    @Column(name = "proof_document_file_path")
    private String proofDocumentFilePath;

    @Column(name = "relation_document_file_path")
    private String relationDocumentFilePath;

    @Column(name = "account_type")
    private String accountType; // KRW, USD 등

    @Column(name = "account_number")
    private String accountNumber; // 선택 계좌

    @Column(name = "available_balance", precision = 18, scale = 4)
    private BigDecimal availableBalance; // 잔액 확인용

    @Column(name = "transfer_amount", precision = 18, scale = 4)
    private BigDecimal transferAmount; // 출금 금액

    @Column(name = "withdrawal_method")
    private String withdrawalMethod; // 예: 계좌이체, 온라인뱅킹 등

    // 편의 메서드: ForeignTransferTransaction과 양방향 설정
    public void setForeignTransferTransaction(ForeignTransferTransaction transaction) {
        this.foreignTransferTransaction = transaction;
        if (transaction.getSender() != this) {
            transaction.setSender(this);
        }
    }
}