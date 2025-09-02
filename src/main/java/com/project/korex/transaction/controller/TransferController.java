package com.project.korex.transaction.controller;

import com.project.korex.transaction.dto.request.TransferRequestDto;
import com.project.korex.transaction.dto.response.TransferResponseDto;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.transaction.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/execute")
    public ResponseEntity<TransferResponseDto> executeTransfer(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody TransferRequestDto request) {

        TransferResponseDto response = transferService.executeTransfer(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
