package com.project.korex.user.repository.jpa;

import com.project.korex.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    @Query("SELECT m FROM Member m JOIN FETCH m.role WHERE m.loginId = :loginId")
    Optional<User> findByLoginIdWithRole(@Param("loginId") String loginId);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findMemberByNickname(String nickname);
}
