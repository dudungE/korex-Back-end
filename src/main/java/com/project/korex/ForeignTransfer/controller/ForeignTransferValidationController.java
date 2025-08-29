package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.MultiBalanceCheckRequest;
import com.project.korex.ForeignTransfer.dto.response.MultiBalanceCheckResponse;
import com.project.korex.ForeignTransfer.service.ForeignTransferValidationService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ForeignTransfer")
@RequiredArgsConstructor
public class ForeignTransferValidationController {

    private final ForeignTransferValidationService validationService;

    @PostMapping("/check-balance")
    public ResponseEntity<MultiBalanceCheckResponse> checkBalance(@RequestBody MultiBalanceCheckRequest request) {
        MultiBalanceCheckResponse response = validationService.checkBalance(request);
        return ResponseEntity.ok(response);
    }
}

