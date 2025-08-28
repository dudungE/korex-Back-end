package com.project.korex.user.repository.jpa;

import com.project.korex.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByLoginId(String loginId);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmailAndName(String email, String name);

    @Query("SELECT u FROM Users u JOIN FETCH u.role WHERE u.loginId = :loginId")
    Optional<Users> findByLoginIdWithRole(@Param("loginId") String loginId);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long loginId);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, Long loginId);

    Optional<Users> findByPhone(String phone);

    boolean existsByName(String name);

    Optional<Users> findByPhoneAndName(String phoneNumber, String name);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByRestrictedFalseAndCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByRestrictedTrueAndRestrictedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Users> findTop3ByRestrictedOrderByRestrictedAtDesc(boolean restricted);
}
