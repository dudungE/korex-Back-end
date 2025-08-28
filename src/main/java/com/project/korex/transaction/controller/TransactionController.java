package com.project.korex.transaction.controller;

import com.project.korex.transaction.dto.response.TransactionResponseDto;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionHistory(@PathVariable Long userId) {
        List<TransactionResponseDto> transactions = transactionService.getUserTransactions(userId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
