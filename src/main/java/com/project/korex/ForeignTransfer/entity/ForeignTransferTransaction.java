 package com.project.korex.ForeignTransfer.entity;

 import com.project.korex.common.BaseEntity;
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
 public class ForeignTransferTransaction extends ForeignTransferEntity {

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

 }