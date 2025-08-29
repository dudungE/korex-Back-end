package com.project.korex.ForeignTransfer.dto.response;

import com.project.korex.ForeignTransfer.entity.Sender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SenderResponse {

    private Long id;
    private String name;
    private String transferReason;
    private String countryNumber;
    private String phoneNumber;
    private String email;
    private String country;
    private String engAddress;
    private String staffMessage;
    private String relationRecipient;
    private String idFilePath;
    private String proofDocumentFilePath;
    private String relationDocumentFilePath;

    // 엔티티 -> DTO 변환용 정적 메서드
    public static SenderResponse fromEntity(Sender sender) {
        SenderResponse dto = new SenderResponse();
        dto.setId(sender.getId());
        dto.setName(sender.getName());
        dto.setTransferReason(sender.getTransferReason());
        dto.setCountryNumber(sender.getCountryNumber());
        dto.setPhoneNumber(sender.getPhoneNumber());
        dto.setEmail(sender.getEmail());
        dto.setCountry(sender.getCountry());
        dto.setEngAddress(sender.getEngAddress());
        dto.setStaffMessage(sender.getStaffMessage());
        dto.setRelationRecipient(sender.getRelationRecipient());
        dto.setIdFilePath(sender.getIdFilePath());
        dto.setProofDocumentFilePath(sender.getProofDocumentFilePath());
        dto.setRelationDocumentFilePath(sender.getRelationDocumentFilePath());
        return dto;
    }
}
