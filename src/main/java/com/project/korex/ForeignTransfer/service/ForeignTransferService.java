package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.ForeignTransferRequest;
import com.project.korex.ForeignTransfer.dto.request.TransferExchangeRequest;
import com.project.korex.ForeignTransfer.dto.response.ForeignTransferResponse;
import com.project.korex.ForeignTransfer.dto.response.TransferExchangeResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.ForeignTransfer.entity.TermsAgreement;
import com.project.korex.ForeignTransfer.enums.RequestStatus;
import com.project.korex.ForeignTransfer.enums.TransferStatus;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.ForeignTransfer.repository.TermsAgreementRepository;
import com.project.korex.transaction.dto.response.ExchangeSimulationDto;
import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.AccountType;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.repository.BalanceRepository;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.transaction.service.ExchangeService;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ForeignTransferService {

    private final UserJpaRepository userRepository;
    private final ForeignTransferTransactionRepository transactionRepository;
    private final TermsAgreementRepository termsRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository globalTransactionRepository;
    private final FileUploadService fileUploadService;
    private final CurrencyRepository currencyRepository;
    private final TransferExchangeService transferExchangeService;

    @Transactional
    public ForeignTransferResponse processFullForeignTransfer(
            String loginId,
            ForeignTransferRequest request,
            MultipartFile idFile,
            MultipartFile proofDocumentFile,
            MultipartFile relationDocumentFile
    ) {

        // 1️⃣ 유저 조회
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2️⃣ 금액 처리
        BigDecimal transferAmount = request.getTransferAmount();
        if (transferAmount == null || transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("송금 금액이 유효하지 않습니다.");
        }

        AccountType accountType = request.getAccountType() != null ? request.getAccountType() : AccountType.KRW;

        Balance krwBalance = balanceRepository.findByUserIdAndAccountType(user.getId(), AccountType.KRW)
                .orElseThrow(() -> new RuntimeException("원화 계좌가 없습니다."));

        Balance targetBalance = null;
        BigDecimal convertedAmount = transferAmount;
        BigDecimal appliedRate = BigDecimal.ONE;
        BigDecimal feeAmount = BigDecimal.ZERO;
        BigDecimal totalKRWDeduct = transferAmount;
        BigDecimal feePercentage = new BigDecimal("0.01"); // 1% 기본값

        Set<String> supportedCurrencies = Set.of("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY");
        Set<String> hundredUnitCurrencies = Set.of("JPY");

        if (accountType == AccountType.FOREIGN && request.getCurrencyCode() != null) {
            String currencyCode = request.getCurrencyCode();
            if (!supportedCurrencies.contains(currencyCode)) {
                throw new RuntimeException(currencyCode + "는 지원하지 않는 통화입니다.");
            }

            targetBalance = balanceRepository.findByUserIdAndCurrency_Code(user.getId(), currencyCode)
                    .orElseThrow(() -> new RuntimeException(currencyCode + " 외화 계좌가 없습니다."));

            // DTO 기반 환전 서비스 호출
            TransferExchangeRequest exchangeRequest = TransferExchangeRequest.builder()
                    .fromCurrency("KRW")
                    .toCurrency(currencyCode)
                    .amount(transferAmount)
                    .build();

            TransferExchangeResponse exchangeResult = transferExchangeService.simulateExchange(exchangeRequest, feePercentage);

            appliedRate = exchangeResult.getExchangeRate();
            convertedAmount = exchangeResult.getToAmount();

            // JPY 등 100단위 통화는 소수점 0자리
            if (hundredUnitCurrencies.contains(currencyCode)) {
                convertedAmount = convertedAmount.setScale(0, RoundingMode.HALF_UP);
            } else {
                convertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
            }

            feeAmount = exchangeResult.getFee();
            totalKRWDeduct = transferAmount.add(feeAmount);

            // 최소 수수료 2000원 적용
            BigDecimal minimumFee = new BigDecimal("2000");
            if (feeAmount.compareTo(minimumFee) < 0) {
                feeAmount = minimumFee;
                totalKRWDeduct = transferAmount.add(feeAmount);
            }

            // 원화 출금
            if (krwBalance.getAvailableAmount().compareTo(totalKRWDeduct) < 0) {
                throw new RuntimeException("원화 잔액 부족");
            }
            krwBalance.setAvailableAmount(krwBalance.getAvailableAmount().subtract(totalKRWDeduct));
            krwBalance.setHeldAmount(krwBalance.getHeldAmount().add(totalKRWDeduct));
            balanceRepository.save(krwBalance);

            // 외화 hold
            if (targetBalance.getAvailableAmount().compareTo(convertedAmount) < 0) {
                throw new RuntimeException(currencyCode + " 잔액 부족");
            }
            targetBalance.setAvailableAmount(targetBalance.getAvailableAmount().subtract(convertedAmount));
            targetBalance.setHeldAmount(targetBalance.getHeldAmount().add(convertedAmount));
            balanceRepository.save(targetBalance);

        } else if (accountType == AccountType.KRW) {
            // 원화 송금
            feePercentage = transferExchangeService.getFeePercentage();

            feeAmount = transferExchangeService.getFeePercentage()
                    .multiply(transferAmount)
                    .setScale(0, RoundingMode.HALF_UP);

            BigDecimal minimumFee = new BigDecimal("2000");
            if (feeAmount.compareTo(minimumFee) < 0) {
                feeAmount = minimumFee;
            }

            totalKRWDeduct = transferAmount.add(feeAmount);

            if (krwBalance.getAvailableAmount().compareTo(totalKRWDeduct) < 0) {
                throw new RuntimeException("원화 잔액 부족");
            }
            krwBalance.setAvailableAmount(krwBalance.getAvailableAmount().subtract(totalKRWDeduct));
            krwBalance.setHeldAmount(krwBalance.getHeldAmount().add(totalKRWDeduct));
            balanceRepository.save(krwBalance);
        }

        // 3️⃣ Transaction 생성
        Currency krwCurrency = currencyRepository.findByCode("KRW")
                .orElseThrow(() -> new RuntimeException("KRW 통화 정보가 없습니다."));
        Currency targetCurrency = !"KRW".equals(request.getCurrencyCode()) && request.getCurrencyCode() != null
                ? currencyRepository.findByCode(request.getCurrencyCode())
                .orElseThrow(() -> new RuntimeException(request.getCurrencyCode() + " 통화 정보가 없습니다."))
                : krwCurrency;

        Transaction generalTransaction = Transaction.builder()
                .fromUser(user)
                .toUser(user)
                .transactionType(TransactionType.TRANSFER)
                .sendAmount(transferAmount)
                .receiveAmount(convertedAmount)
                .exchangeRateApplied(appliedRate)
                .feeAmount(feeAmount)
                .feePercentage(feePercentage)
                .totalDeductedAmount(totalKRWDeduct)
                .fromCurrencyCode(krwCurrency)
                .toCurrencyCode(targetCurrency)
                .status("PENDING")
                .build();
        globalTransactionRepository.save(generalTransaction);

        // 4️⃣ ForeignTransferTransaction
        ForeignTransferTransaction ftTransaction = ForeignTransferTransaction.builder()
                .user(user)
                .transaction(generalTransaction)
                .requestStatus(RequestStatus.SUBMITTED)
                .transferStatus(TransferStatus.NOT_STARTED)
                .createdAt(LocalDateTime.now())
                .transferAmount(transferAmount)
                .convertedAmount(convertedAmount)
                .exchangeRate(appliedRate)
                .accountPassword(request.getAccountPassword())
                .krwNumber(request.getKrwAccount())
                .foreignNumber(request.getForeignAccount())
                .staffMessage(request.getStaffMessage())
                .relationRecipient(request.getRelationRecipient())
                .transactionType(TransactionType.TRANSFER)
                .build();
        transactionRepository.save(ftTransaction);

        // 5️⃣ Sender
        Sender sender = Sender.builder()
                .user(user)
                .foreignTransferTransaction(ftTransaction)
                .name(request.getSenderName())
                .transferReason(request.getTransferReason())
                .countryNumber(request.getCountryNumber())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .country(request.getCountry())
                .engAddress(request.getEngAddress())
                .relationRecipient(request.getRelationRecipient())
                .accountType(accountType.name())
                .accountNumber(request.getAccountNumber())
                .idFilePath(saveFilePath(ftTransaction, idFile, "ID"))
                .proofDocumentFilePath(saveFilePath(ftTransaction, proofDocumentFile, "PROOF"))
                .relationDocumentFilePath(saveFilePath(ftTransaction, relationDocumentFile, "RELATION"))
                .build();

        ftTransaction.setSender(sender);
        transactionRepository.save(ftTransaction);

        // 6️⃣ TermsAgreement
        TermsAgreement agreement = TermsAgreement.builder()
                .foreignTransferTransaction(ftTransaction)
                .agree1(request.isAgree1())
                .agree2(request.isAgree2())
                .agree3(request.isAgree3())
                .agreedAt(LocalDateTime.now())
                .build();
        termsRepository.save(agreement);

        // 7️⃣ Response 반환
        return ForeignTransferResponse.builder()
                .transferId(ftTransaction.getId())
                .senderId(sender.getId())
                .accountType(accountType.name())
                .transferAmount(transferAmount)
                .convertedAmount(convertedAmount)
                .appliedRate(appliedRate)
                .feeAmount(feeAmount)
                .feePercentage(feePercentage)
                .transferReason(request.getTransferReason())
                .relationRecipient(request.getRelationRecipient())
                .requestStatus(ftTransaction.getRequestStatus().name())
                .transferStatus(ftTransaction.getTransferStatus().name())
                .createdAt(ftTransaction.getCreatedAt())
                .agreedAt(agreement.getAgreedAt())
                .build();
    }

    private String saveFilePath(ForeignTransferTransaction transaction, MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) return null;
        return fileUploadService.uploadFileToTransaction(transaction, file, fileType).getFileUrl();
    }
}
