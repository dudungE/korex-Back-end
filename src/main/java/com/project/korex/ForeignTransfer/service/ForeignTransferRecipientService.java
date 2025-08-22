package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.RecipientRequest;
import com.project.korex.ForeignTransfer.dto.response.RecipientResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferRecipient;
import com.project.korex.ForeignTransfer.repository.ForeignTransferRecipientRepository;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForeignTransferRecipientService {

    private final ForeignTransferRecipientRepository recipientRepository;
    private final UserJpaRepository userRepository;
    private final CurrencyRepository currencyRepository;

    private Currency getCurrencyByName(String currencyName) {
        return currencyRepository.findByCurrencyName(currencyName)
                .orElseThrow(() -> new NoSuchElementException("해당 통화를 찾을 수 없습니다: " + currencyName));
    }

    @Transactional
    public RecipientResponse createRecipient(Long userId, RecipientRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + userId));

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
                .orElseThrow(() -> new NoSuchElementException("수취인 정보를 찾을 수 없습니다. id=" + recipientId));

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
                .orElseThrow(() -> new NoSuchElementException("수취인 정보를 찾을 수 없습니다. id=" + recipientId));

        recipient.setName(request.getName()); // ← 이 줄 추가
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
                .orElseThrow(() -> new NoSuchElementException("수취인 정보를 찾을 수 없습니다. id=" + recipientId));

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
