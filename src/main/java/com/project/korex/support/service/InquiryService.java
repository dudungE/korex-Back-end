package com.project.korex.support.service;

import com.project.korex.support.dto.InquiryCreateRequest;
import com.project.korex.support.dto.InquiryResponse;
import com.project.korex.support.entity.Inquiry;
import com.project.korex.support.repository.jpa.InquiryJpaRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryJpaRepository inquiryJpaRepository;
    private final UserJpaRepository userJpaRepository;

    /**
     * 문의 등록
     * @return 생성된 문의 ID
     */
    @Transactional
    public Long createInquiry(Long userId, InquiryCreateRequest req) {
        Users user = userJpaRepository.getReferenceById(userId);

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .category(req.getCategory())
                .title(req.getTitle().trim())
                .content(req.getContent().trim())
                .build();

        Inquiry saved = inquiryJpaRepository.save(inquiry);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public Page<InquiryResponse> getMyInquiries(Long userId, Pageable pageable) {
        return inquiryJpaRepository
                .findByUserId(userId, pageable)
                .map(InquiryResponse::from);
    }

    @Transactional(readOnly = true)
    public InquiryResponse getMyInquirySummary(Long userId, Long inquiryId) {
        var inq = inquiryJpaRepository.findByIdAndUserId(inquiryId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
        return InquiryResponse.from(inq);
    }
}
