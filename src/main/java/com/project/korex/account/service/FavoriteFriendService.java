package com.project.korex.account.service;

import com.project.korex.account.dto.request.AddFriendsRequestDto;
import com.project.korex.account.dto.request.ReorderFavoriteRequestDto;
import com.project.korex.account.dto.response.FavoriteFriendsResponseDto;
import com.project.korex.account.entity.FavoriteFriend;
import com.project.korex.account.exception.DuplicateFavoriteException;
import com.project.korex.account.exception.FavoriteLimitExceededException;
import com.project.korex.account.exception.FavoriteNotFoundException;
import com.project.korex.account.exception.InvalidRequestException;
import com.project.korex.account.repository.FavoriteFriendRepository;
import com.project.korex.common.code.ErrorCode;
import com.project.korex.common.exception.UserNotFoundException;
import com.project.korex.transaction.service.TransactionService;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteFriendService {

    private final FavoriteFriendRepository favoriteFriendRepository;
    private final UserJpaRepository userRepository;
    private final TransactionService transactionService;

    /**
     * 사용자의 즐겨찾기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FavoriteFriendsResponseDto> getFavoriteFriends(Long userId) {
        List<FavoriteFriend> favorites = favoriteFriendRepository
                .findByUserIdOrderByDisplayOrderAsc(userId);

        return favorites.stream()
                .map(this::toFavoriteFriendResponse)
                .collect(Collectors.toList());
    }

    /**
     * 친구 즐겨찾기에 추가
     */
    public FavoriteFriendsResponseDto addFavoriteFriend(Long userId, AddFriendsRequestDto request) {
        // 1. 현재 사용자 조회
        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 2. 친구 존재 여부 확인
        Users friend = userRepository.findByPhoneAndName(
                        request.getPhoneNumber(), request.getName())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 3. 본인을 추가하려는지 확인
        if (friend.getId().equals(userId)) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
        }

        // 4. 이미 즐겨찾기에 있는지 확인
        if (favoriteFriendRepository.existsByUser_IdAndFriend_Id(userId, friend.getId())) {
            throw new DuplicateFavoriteException(ErrorCode.DUPLICATE_FAVORITE);
        }

        // 5. 즐겨찾기 개수 제한 확인 (최대 4명)
        long favoriteCount = favoriteFriendRepository.countByUserId(userId);
        if (favoriteCount >= 4) {
            throw new FavoriteLimitExceededException(ErrorCode.FAVORITE_LIMIT_EXCEED);
        }

        // 6. 마지막 송금일 조회 (있다면)
        LocalDateTime lastTransferDate = transactionService
                .getLastTransferDate(userId, friend.getId());

        // 7. 즐겨찾기 생성 및 저장 - User 객체 설정
        FavoriteFriend favoriteFriend = FavoriteFriend.builder()
                .user(currentUser)                 // User 객체 설정
                .friend(friend)                    // User 객체 설정
                .displayOrder((int) favoriteCount + 1)
                .lastTransferDate(lastTransferDate)
                .build();

        FavoriteFriend savedFavorite = favoriteFriendRepository.save(favoriteFriend);

        return toFavoriteFriendResponse(savedFavorite);
    }


    /**
     * 즐겨찾기 삭제
     */
    public void deleteFavoriteFriend(Long userId, Long favoriteId) {
        FavoriteFriend favorite = favoriteFriendRepository
                .findByIdAndUserId(favoriteId, userId)
                .orElseThrow(() -> new FavoriteNotFoundException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteFriendRepository.delete(favorite);

        // 순서 재정렬
        reorderFavoritesAfterDeletion(userId);
    }

    /**
     * 즐겨찾기 순서 변경
     */
    public void reorderFavorites(Long userId, ReorderFavoriteRequestDto request) {
        List<FavoriteFriend> favorites = favoriteFriendRepository
                .findByUser_IdAndIdIn(userId, request.getFavoriteIds());

        // 요청된 ID 개수와 실제 조회된 개수가 다르면 에러
        if (favorites.size() != request.getFavoriteIds().size()) {
//            throw new FavoriteNotFoundException("일부 즐겨찾기를 찾을 수 없습니다");
        }

        // 순서 업데이트
        for (int i = 0; i < request.getFavoriteIds().size(); i++) {
            Long favoriteId = request.getFavoriteIds().get(i);
            final int index =  i;
            favorites.stream()
                    .filter(f -> f.getId().equals(favoriteId))
                    .findFirst()
                    .ifPresent(f -> f.updateDisplayOrder(index + 1));
        }
    }

    /**
     * 닉네임 수정
     */
//    public FavoriteFriendResponse updateNickname(Long userId, Long favoriteId, UpdateNicknameRequest request) {
//        FavoriteFriend favorite = favoriteFriendRepository
//                .findByFavoriteIdAndUserId(favoriteId, userId)
//                .orElseThrow(() -> new FavoriteNotFoundException("즐겨찾기를 찾을 수 없습니다"));
//
//        favorite.updateNickname(request.getNickname());
//
//        return toFavoriteFriendResponse(favorite);
//    }

    /**
     * 송금 완료 시 마지막 송금일 업데이트
     */
    public void updateLastTransferDate(Long userId, Long friendUserId) {
        favoriteFriendRepository
                .findByUser_IdAndFriend_Id(userId, friendUserId)
                .ifPresent(favorite -> {
                    favorite.updateLastTransferDate(LocalDateTime.now());
                });
    }


    /**
     * 삭제 후 순서 재정렬
     */
    private void reorderFavoritesAfterDeletion(Long userId) {
        List<FavoriteFriend> favorites = favoriteFriendRepository
                .findByUserIdOrderByDisplayOrderAsc(userId);

        for (int i = 0; i < favorites.size(); i++) {
            favorites.get(i).updateDisplayOrder(i + 1);
        }
    }

    /**
     * FavoriteFriend를 FavoriteFriendResponse로 변환
     */
    private FavoriteFriendsResponseDto toFavoriteFriendResponse(FavoriteFriend favorite) {
        Users friend = userRepository.findById(favorite.getId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        return FavoriteFriendsResponseDto.builder()
                .favoriteId(favorite.getId())
                .friendUserId(friend.getId())
                .realName(friend.getName())
                .phoneNumber(maskPhoneNumber(friend.getPhone()))
                .icon(generateUserIcon(friend.getName()))
                .lastTransfer(formatLastTransferDate(favorite.getLastTransferDate()))
                .displayOrder(favorite.getDisplayOrder())
                .build();
    }

    /**
     * 전화번호 마스킹 처리
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() >= 13) {
            return phoneNumber.substring(0, 4) + "****-" + phoneNumber.substring(9);
        }
        return phoneNumber;
    }

    /**
     * 사용자 아이콘 생성
     */
    private String generateUserIcon(String name) {
        String[] icons = {"👨", "👩", "👦", "👧", "🧑", "👴", "👵"};
        int index = Math.abs(name.hashCode()) % icons.length;
        return icons[index];
    }

    /**
     * 마지막 송금일 포맷팅
     */
    private String formatLastTransferDate(LocalDateTime lastTransferDate) {
        if (lastTransferDate == null) return "방금 추가됨";

        LocalDateTime now = LocalDateTime.now();
        long daysDiff = ChronoUnit.DAYS.between(lastTransferDate, now);

        if (daysDiff == 0) return "오늘 송금";
        else if (daysDiff == 1) return "어제 송금";
        else return daysDiff + "일 전 송금";
    }
}

