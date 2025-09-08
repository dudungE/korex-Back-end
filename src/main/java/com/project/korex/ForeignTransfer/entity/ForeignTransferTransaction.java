 package com.project.korex.ForeignTransfer.entity;

 import com.project.korex.transaction.entity.Transaction;
 import jakarta.persistence.*;
 import lombok.Getter;
 import lombok.Setter;
 import org.springframework.data.annotation.CreatedDate;
 import org.springframework.data.annotation.LastModifiedDate;

 import java.time.LocalDateTime;

 @Entity
 @Table(name = "ForeignTransferTransaction")
 @Getter
 @Setter
 public class ForeignTransferTransaction {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "transfer_id")
     private Long id;

     @OneToOne
     @JoinColumn(name = "transaction_id")
     private Transaction transaction;

     @OneToOne
     @JoinColumn(name = "recipient_id")
     private ForeignTransferRecipient recipient;

     @OneToOne(mappedBy = "foreignTransferTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
     private ForeignTransferSender sender;

     @OneToOne(mappedBy = "foreignTransferTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
     private ForeignTransferTermsAgreement termsAgreement;

     @Column(name = "compliance_status")
     private String status;

     @CreatedDate
     @Column(updatable = false)
     private LocalDateTime createdAt;

     @LastModifiedDate
     private LocalDateTime updatedAt;

     @CreatedDate
     @Column(updatable = false)
     private LocalDateTime EstimatedArrivalDate; // 해외송금 예상 도착일

     @LastModifiedDate
     private LocalDateTime ActualArrivalDate; // 해외송금 실제 도착일

 }