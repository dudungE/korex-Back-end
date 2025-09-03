package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.SenderRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderResponse;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.ForeignTransfer.service.SenderService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foreign-transfer/sender")
@RequiredArgsConstructor
public class SenderController {

    private final SenderService senderService;

    @PostMapping("/create")
    public ResponseEntity<SenderResponse> createSender(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ModelAttribute SenderRequest request
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loginId = principal.getName();
        Sender sender = senderService.createSenderWithTransaction(loginId, request);

        SenderResponse response = new SenderResponse();
        response.setId(sender.getId());
        response.setName(sender.getName());
        response.setTransferReason(sender.getTransferReason());
        response.setCountryNumber(sender.getCountryNumber());
        response.setPhoneNumber(sender.getPhoneNumber());
        response.setEmail(sender.getEmail());
        response.setCountry(sender.getCountry());
        response.setEngAddress(sender.getEngAddress());
        response.setStaffMessage(sender.getStaffMessage());
        response.setRelationRecipient(sender.getRelationRecipient());
        response.setIdFilePath(sender.getIdFilePath());
        response.setProofDocumentFilePath(sender.getProofDocumentFilePath());
        response.setRelationDocumentFilePath(sender.getRelationDocumentFilePath());

        return ResponseEntity.ok(response);
    }
}
