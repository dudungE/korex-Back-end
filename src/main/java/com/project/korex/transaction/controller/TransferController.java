package com.project.korex.transaction.controller;

import com.project.korex.transaction.dto.request.TransferCalculationRequestDto;
import com.project.korex.transaction.dto.request.TransferExecutionRequestDto;
import com.project.korex.transaction.dto.response.RecipientResponseDto;
import com.project.korex.transaction.dto.response.TransferCalculationResponseDto;
import com.project.korex.transaction.dto.response.TransferExecutionResponseDto;
import com.project.korex.transaction.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    // 송금 금액 계산
    @PostMapping("/calculate")
    public ResponseEntity<TransferCalculationResponseDto> calculateTransfer(
            @RequestBody TransferCalculationRequestDto request) {
        TransferCalculationResponseDto calculation = transferService.calculateTransfer(request);
        return new ResponseEntity<>(calculation,HttpStatus.OK);
    }

    // 친구간 송금 실행
    @PostMapping("/execute")
    public ResponseEntity<TransferExecutionResponseDto> executeTransfer(
            @RequestBody @Valid TransferExecutionRequestDto request) {
        TransferExecutionResponseDto response = transferService.executeTransfer(request);
        return ResponseEntity.ok(response);
    }


    // 수취인 검색
    @GetMapping("/recipient/search")
    public ResponseEntity<RecipientResponseDto> searchRecipient(@RequestParam String phone) {
        RecipientResponseDto recipient = transferService.findRecipientByPhone(phone);
        return new ResponseEntity<>(recipient, HttpStatus.OK);
    }
}
