package com.project.korex.account.repository;

import com.project.korex.account.entity.FavoriteFriend;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteFriendRepository extends JpaRepository<FavoriteFriend, Long> {

    /**
     * 사용자의 즐겨찾기 목록 조회 (순서별 정렬)
     */
    List<FavoriteFriend> findByUserIdOrderByDisplayOrderAsc(Long userId);

    /**
     * 사용자의 즐겨찾기 개수 조회
     */
    long countByUserId(Long userId);

    /**
     * 특정 즐겨찾기 조회 (사용자 검증용)
     */
    Optional<FavoriteFriend> findByFavoriteIdAndUserId(Long favoriteId, Long userId);

    /**
     * 중복 확인 (같은 친구가 이미 즐겨찾기에 있는지)
     */
    boolean existsByUserIdAndFriendUserId(Long userId, Long friendUserId);

    /**
     * 친구와의 즐겨찾기 관계 조회
     */
    Optional<FavoriteFriend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

    /**
     * 여러 즐겨찾기 ID로 조회 (순서 변경용)
     */
    List<FavoriteFriend> findByUserIdAndFavoriteIdIn(Long userId, List<Long> favoriteIds);

    /**
     * 사용자 삭제 시 관련 즐겨찾기도 삭제
     */
//    @Modifying
//    @Query("DELETE FROM FavoriteFriend f WHERE f.userId = :userId")
//    void deleteByUserId(@Param("userId") Long userId);
//
//    @Modifying
//    @Query("DELETE FROM FavoriteFriend f WHERE f.friendUserId = :friendUserId")
//    void deleteByFriendUserId(@Param("friendUserId") Long friendUserId);

    /**
     * 사용자의 즐겨찾기를 표시 순서로 조회 (페이징)
     */
    Page<FavoriteFriend> findByUserIdOrderByDisplayOrderAsc(Long userId, Pageable pageable);

    /**
     * 특정 표시 순서 이후의 즐겨찾기들 조회 (순서 재정렬용)
     */
    @Query("SELECT f FROM FavoriteFriend f WHERE f.userId = :userId AND f.displayOrder > :order ORDER BY f.displayOrder ASC")
    List<FavoriteFriend> findByUserIdAndDisplayOrderGreaterThanOrderByDisplayOrderAsc(
            @Param("userId") Long userId,
            @Param("order") Integer order);
}

