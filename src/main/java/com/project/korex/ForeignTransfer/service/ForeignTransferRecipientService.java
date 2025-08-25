package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.RecipientRequest;
import com.project.korex.ForeignTransfer.dto.response.RecipientResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferRecipient;
import com.project.korex.ForeignTransfer.exception.CurrencyNotFoundException;
import com.project.korex.ForeignTransfer.exception.RecipientNotFoundException;
import com.project.korex.ForeignTransfer.repository.ForeignTransferRecipientRepository;
import com.project.korex.common.code.ErrorCode;
import com.project.korex.common.exception.UserNotFoundException;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForeignTransferRecipientService {

    private final ForeignTransferRecipientRepository recipientRepository;
    private final UserJpaRepository userRepository;
    private final CurrencyRepository currencyRepository;

    private Currency getCurrencyByName(String currencyName) {
        return currencyRepository.findByCurrencyName(currencyName)
                .orElseThrow(() -> new CurrencyNotFoundException(ErrorCode.CURRENCY_NOT_FOUND));
    }

    @Transactional
    public RecipientResponse createRecipient(Long userId, RecipientRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        ForeignTransferRecipient recipient = new ForeignTransferRecipient();
        recipient.setUser(user);
        recipient.setName(request.getName());
        recipient.setBankName(request.getBankName());
        recipient.setAccountNumber(request.getAccountNumber());
        recipient.setCountryNumber(request.getCountryNumber());
        recipient.setCountry(request.getCountry());
        recipient.setPhoneNumber(request.getPhoneNumber());
        recipient.setEmail(request.getEmail());
        recipient.setRelationRecipient(request.getRelationRecipient());
        recipient.setCurrency(getCurrencyByName(request.getCurrency()));
        recipient.setEngAddress(request.getEngAddress());

        ForeignTransferRecipient savedRecipient = recipientRepository.save(recipient);

        return convertToResponse(savedRecipient);
    }


    @Transactional
    public RecipientResponse getRecipientById(Long userId, Long recipientId) {
        ForeignTransferRecipient recipient = recipientRepository
                .findByIdAndUser_Id(recipientId, userId)
                .orElseThrow(() -> new RecipientNotFoundException(ErrorCode.RECIPIENT_NOT_FOUND));

        return convertToResponse(recipient);
    }

    @Transactional
    public List<RecipientResponse> getRecipientsByUserId(Long userId) {
        List<ForeignTransferRecipient> recipients = recipientRepository.findByUser_Id(userId);
        return recipients.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecipientResponse updateRecipient(Long userId, Long recipientId, RecipientRequest request) {
        ForeignTransferRecipient recipient = recipientRepository.findByIdAndUser_Id(recipientId, userId)
                .orElseThrow(() -> new RecipientNotFoundException(ErrorCode.RECIPIENT_NOT_FOUND));

        recipient.setName(request.getName());
        recipient.setBankName(request.getBankName());
        recipient.setAccountNumber(request.getAccountNumber());
        recipient.setCountryNumber(request.getCountryNumber());
        recipient.setCountry(request.getCountry());
        recipient.setPhoneNumber(request.getPhoneNumber());
        recipient.setEmail(request.getEmail());
        recipient.setRelationRecipient(request.getRelationRecipient());
        recipient.setCurrency(getCurrencyByName(request.getCurrency()));
        recipient.setEngAddress(request.getEngAddress());

        return convertToResponse(recipient);
    }

    @Transactional
    public void deleteRecipient(Long userId, Long recipientId) {
        ForeignTransferRecipient recipient = recipientRepository.findByIdAndUser_Id(recipientId, userId)
                .orElseThrow(() -> new RecipientNotFoundException(ErrorCode.RECIPIENT_NOT_FOUND));

        recipientRepository.delete(recipient);
    }

    private RecipientResponse convertToResponse(ForeignTransferRecipient recipient) {
        RecipientResponse response = new RecipientResponse();

        response.setRecipientId(recipient.getId());
        response.setName(recipient.getName());
        response.setCurrency(recipient.getCurrency() != null ? recipient.getCurrency().getCurrencyName() : null);
        response.setBankName(recipient.getBankName());
        response.setAccountNumber(recipient.getAccountNumber());
        response.setCountryNumber(recipient.getCountryNumber());
        response.setCountry(recipient.getCountry());
        response.setPhoneNumber(recipient.getPhoneNumber());
        response.setEmail(recipient.getEmail());
        response.setRelationRecipient(recipient.getRelationRecipient());
        response.setEngAddress(recipient.getEngAddress());

        return response;
    }


}
