// ForeignTransferTransactionFactory.java
package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.enums.RequestStatus;
import com.project.korex.ForeignTransfer.enums.TransferStatus;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForeignTransferTransactionFactory {

    private final TransactionRepository generalTransactionRepository;
    private final ForeignTransferTransactionRepository transactionRepository;

    public ForeignTransferTransaction createTransactionForUser(Users user, String bankName, String krwNumber, String foreignNumber, String accountPassword) {
        // 1️⃣ 일반 Transaction 생성
        Transaction generalTransaction = Transaction.builder()
                .fromUser(user)
                .toUser(user)
                .transactionType(TransactionType.TRANSFER)
                .status("PENDING")
                .build();
        generalTransactionRepository.save(generalTransaction);

        // 2️⃣ ForeignTransferTransaction 생성
        ForeignTransferTransaction transaction = ForeignTransferTransaction.builder()
                .transaction(generalTransaction)
                .user(user)
                .requestStatus(RequestStatus.NOT_STARTED)
                .transferStatus(TransferStatus.NOT_STARTED)
                .krwNumber(krwNumber)
                .foreignNumber(foreignNumber)
                .accountPassword(accountPassword)
                .build();
        transactionRepository.save(transaction);

        return transaction;
    }
}
