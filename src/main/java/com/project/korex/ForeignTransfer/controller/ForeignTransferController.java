package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.SenderTransferRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderTransferResponse;
import com.project.korex.ForeignTransfer.service.ForeignTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/foreign-transfer")
@RequiredArgsConstructor
public class ForeignTransferController {

    private final ForeignTransferService foreignTransferService;

    /**
     * 해외 송금 생성
     */
    @PostMapping
    public ResponseEntity<SenderTransferResponse> createForeignTransfer(
            @RequestBody SenderTransferRequest request
    ) {
        SenderTransferResponse response = foreignTransferService.createForeignTransfer(request);
        return ResponseEntity.ok(response);
    }
}
