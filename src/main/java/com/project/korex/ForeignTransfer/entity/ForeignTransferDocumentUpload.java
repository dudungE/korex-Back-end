package com.project.korex.ForeignTransfer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ForeignTransferDocumentUpload")
@Getter
@Setter
public class ForeignTransferDocumentUpload extends ForeignTransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private ForeignTransferTransaction foreignTransferTransaction;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "stored_filename")
    private String storedFilename;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size")
    private int fileSize;
}