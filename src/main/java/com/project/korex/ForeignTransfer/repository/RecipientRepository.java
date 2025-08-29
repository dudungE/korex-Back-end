package com.project.korex.ForeignTransfer.repository;

import com.project.korex.ForeignTransfer.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    // 사용자 ID로 해당 사용자가 등록한 모든 수취인 정보를 조회
    List<Recipient> findByUser_Id(Long userId);

    // 특정 수취인 조회 + 로그인 사용자 소유 확인
    Optional<Recipient> findByIdAndUser_Id(Long id, Long userId);

    // 활성화된 수취인 조회
    List<Recipient> findByUser_IdAndIsActiveTrue(Long userId);

}