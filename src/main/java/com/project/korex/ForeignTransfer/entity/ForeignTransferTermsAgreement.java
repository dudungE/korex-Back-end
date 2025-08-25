package com.project.korex.ForeignTransfer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ForeignTransferTermsAgreement")
@Getter
@Setter
public class ForeignTransferTermsAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "transfer_id")
    private ForeignTransferTransaction foreignTransferTransaction;

    @Column(name = "agreed")
    private Boolean agreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

}