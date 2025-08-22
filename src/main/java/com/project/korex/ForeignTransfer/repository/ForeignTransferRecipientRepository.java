package com.project.korex.ForeignTransfer.repository;

import com.project.korex.ForeignTransfer.entity.ForeignTransferRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForeignTransferRecipientRepository extends JpaRepository<ForeignTransferRecipient, Long> {

    // 사용자 ID로 해당 사용자가 등록한 모든 수취인 정보를 조회
    List<ForeignTransferRecipient> findByUser_Id(Long userId);

    // 특정 수취인 조회 + 로그인 사용자 소유 확인
    Optional<ForeignTransferRecipient> findByIdAndUser_Id(Long id, Long userId);

}