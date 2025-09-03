package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.SenderTransferRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderTransferResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.ForeignTransfer.enums.RequestStatus;
import com.project.korex.ForeignTransfer.enums.TransferStatus;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.transaction.dto.response.ExchangeSimulationDto;
import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.enums.AccountType;
import com.project.korex.transaction.repository.BalanceRepository;
import com.project.korex.transaction.service.ExchangeService;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForeignTransferService {

    private final UserJpaRepository userRepository;
    private final BalanceRepository balanceRepository;
    private final ForeignTransferTransactionRepository transactionRepository;
    private final ExchangeService exchangeService;
    private final FileUploadService fileUploadService;

    @Transactional
    public SenderTransferResponse createForeignTransfer(String loginId, SenderTransferRequest dto) {
        // 1️⃣ 유저 조회
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2️⃣ 금액 & 계좌 설정
        BigDecimal transferAmount = dto.getTransferAmount();
        BigDecimal convertedAmount = transferAmount;
        BigDecimal appliedRate = null;
        AccountType type = dto.getAccountType() != null ? dto.getAccountType() : AccountType.KRW;

        Balance krwBalance = balanceRepository.findByUserIdAndAccountType(user.getId(), AccountType.KRW)
                .orElseThrow(() -> new RuntimeException("원화 계좌가 없습니다."));

        Balance targetBalance = null;

        // 3️⃣ 외화 계좌 처리
        if (type == AccountType.FOREIGN && dto.getCurrencyCode() != null) {
            targetBalance = balanceRepository.findByUserIdAndCurrency_Code(user.getId(), dto.getCurrencyCode())
                    .orElseThrow(() -> new RuntimeException(dto.getCurrencyCode() + " 외화 계좌가 없습니다."));

            if (!"KRW".equals(dto.getCurrencyCode())) {
                ExchangeSimulationDto simulation = exchangeService.simulateExchange("KRW", dto.getCurrencyCode(), transferAmount);
                appliedRate = simulation.getExchangeRate();
                convertedAmount = simulation.getToAmount();

                if (krwBalance.getAvailableAmount().compareTo(transferAmount) < 0) {
                    throw new RuntimeException("원화 잔액 부족");
                }
                krwBalance.setAvailableAmount(krwBalance.getAvailableAmount().subtract(transferAmount));
                krwBalance.setHeldAmount(krwBalance.getHeldAmount().add(transferAmount));
            }

            if (targetBalance.getAvailableAmount().compareTo(convertedAmount) < 0) {
                throw new RuntimeException(dto.getCurrencyCode() + " 잔액 부족");
            }
            targetBalance.setAvailableAmount(targetBalance.getAvailableAmount().subtract(convertedAmount));
            targetBalance.setHeldAmount(targetBalance.getHeldAmount().add(convertedAmount));
        }

        // 4️⃣ 트랜잭션 생성
        ForeignTransferTransaction transaction = ForeignTransferTransaction.builder()
                .transferAmount(transferAmount)
                .accountPassword(dto.getAccountPassword())
                .relationRecipient(dto.getRelationRecipient())
                .user(user)
                .krwNumber(user.getKrwAccount())
                .foreignNumber(user.getForeignAccount())
                .requestStatus(RequestStatus.PENDING)
                .transferStatus(TransferStatus.NOT_STARTED)
                .createdAt(LocalDateTime.now())
                .convertedAmount(convertedAmount)
                .exchangeRate(appliedRate)
                .staffMessage(dto.getStaffMessage())
                .build();

        // 5️⃣ Sender 생성
        BigDecimal availableBalance = type == AccountType.KRW
                ? krwBalance.getAvailableAmount()
                : (targetBalance != null ? targetBalance.getAvailableAmount() : BigDecimal.ZERO);

        Sender sender = Sender.builder()
                .user(user)
                .foreignTransferTransaction(transaction) // 양방향 설정
                .name(dto.getSenderName())
                .transferReason(dto.getTransferReason())
                .countryNumber(dto.getCountryNumber())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .country(dto.getCountry())
                .engAddress(dto.getEngAddress())
                .relationRecipient(dto.getRelationRecipient())
                .accountType(type.name())
                .accountNumber(dto.getAccountNumber())
                .availableBalance(availableBalance)
                .transferAmount(transferAmount)
                .withdrawalMethod(dto.getWithdrawalMethod())
                .idFilePath(saveFilePath(transaction, dto.getIdFile(), "ID"))
                .proofDocumentFilePath(saveFilePath(transaction, dto.getProofDocumentFile(), "PROOF"))
                .relationDocumentFilePath(saveFilePath(transaction, dto.getRelationDocumentFile(), "RELATION"))
                .build();

        transaction.setSender(sender);

        // 6️⃣ 트랜잭션 + sender 저장 (cascade로 한번에 저장)
        transactionRepository.save(transaction);

        // 7️⃣ Response 반환
        return SenderTransferResponse.builder()
                .transferId(transaction.getId())
                .senderId(sender.getId())
                .accountType(type.name())
                .availableBalance(sender.getAvailableBalance())
                .transferAmount(transferAmount)
                .convertedAmount(convertedAmount)
                .appliedRate(appliedRate)
                .withdrawalMethod(dto.getWithdrawalMethod())
                .transferReason(dto.getTransferReason())
                .relationRecipient(dto.getRelationRecipient())
                .requestStatus(transaction.getRequestStatus().name())
                .transferStatus(transaction.getTransferStatus().name())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    // 파일 업로드
    private String saveFilePath(ForeignTransferTransaction transaction, MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) return null;
        return fileUploadService.uploadFileToTransaction(transaction, file, fileType).getFileUrl();
    }
}
