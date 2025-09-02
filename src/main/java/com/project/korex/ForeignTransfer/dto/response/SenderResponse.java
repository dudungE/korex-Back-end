package com.project.korex.ForeignTransfer.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
