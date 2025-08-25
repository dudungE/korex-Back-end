package com.project.korex.ForeignTransfer.entity;

import com.project.korex.common.BaseEntity;
import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ForeignTransferSender")
@Getter
@Setter
public class ForeignTransferSender {

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

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "transfer_amount")
    private BigDecimal transferAmount;

    @Column(name = "staff_message")
    private String staffMessage;

    @Column(name = "id_file_path")
    private String idFilePath;

    @Column(name = "proof_document_file_path")
    private String proofDocumentFilePath;

    @Column(name = "relation_document_file_path")
    private String relationDocumentFilePath;
}