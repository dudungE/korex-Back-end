package com.project.korex.account.controller;

import com.project.korex.account.dto.request.AddFriendsRequestDto;
import com.project.korex.account.dto.request.ReorderFavoriteRequestDto;
import com.project.korex.account.dto.response.FavoriteFriendsResponseDto;
import com.project.korex.account.service.FavoriteFriendService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/favorites")
public class FavoriteFriendController {

    private final FavoriteFriendService favoriteFriendService;
    /**
     * 즐겨찾기 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<FavoriteFriendsResponseDto>> getFavoriteFriends(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        Long userId = customUserPrincipal.getUserId();
        log.info("즐겨찾기 목록 조회 요청 - 사용자 ID: {}", userId);

        List<FavoriteFriendsResponseDto> favorites = favoriteFriendService.getFavoriteFriends(userId);

        log.info("즐겨찾기 목록 조회 완료 - 사용자 ID: {}, 개수: {}", userId, favorites.size());
        return new ResponseEntity<>(favorites, HttpStatus.OK);
    }

    /**
     * 친구 즐겨찾기에 추가
     */
    @PostMapping
    public ResponseEntity<FavoriteFriendsResponseDto> addFavoriteFriend(
            @RequestBody @Valid AddFriendsRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        Long userId = customUserPrincipal.getUserId();
        log.info("즐겨찾기 추가 요청 - 사용자 ID: {}, 이름: {}, 전화번호: {}",
                userId, request.getName(), request.getPhoneNumber());

        FavoriteFriendsResponseDto response = favoriteFriendService.addFavoriteFriend(userId, request);

        log.info("즐겨찾기 추가 완료 - 사용자 ID: {}, 즐겨찾기 ID: {}", userId, response.getFavoriteId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 즐겨찾기 삭제
     */
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavoriteFriend(
            @PathVariable Long favoriteId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        Long userId = customUserPrincipal.getUserId();
        log.info("즐겨찾기 삭제 요청 - 사용자 ID: {}, 즐겨찾기 ID: {}", userId, favoriteId);

        favoriteFriendService.deleteFavoriteFriend(userId, favoriteId);

        log.info("즐겨찾기 삭제 완료 - 사용자 ID: {}, 즐겨찾기 ID: {}", userId, favoriteId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    /**
     * 즐겨찾기 순서 변경
     */
    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderFavorites(
            @RequestBody @Valid ReorderFavoriteRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        Long userId = customUserPrincipal.getUserId();
        log.info("즐겨찾기 순서 변경 요청 - 사용자 ID: {}, 순서: {}", userId, request.getFavoriteIds());

        favoriteFriendService.reorderFavorites(userId, request);

        log.info("즐겨찾기 순서 변경 완료 - 사용자 ID: {}", userId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 닉네임 수정
     */
//    @PutMapping("/{favoriteId}/nickname")
//    public ResponseEntity<FavoriteFriendsResponseDto> updateNickname(
//            @PathVariable Long favoriteId,
//            @RequestBody @Valid UpdateNicknameRequest request,
//            Authentication authentication) {
//        Long userId = getCurrentUserId(authentication);
//        log.info("닉네임 수정 요청 - 사용자 ID: {}, 즐겨찾기 ID: {}, 새 닉네임: {}",
//                userId, favoriteId, request.getNickname());
//
//        FavoriteFriendResponse response = favoriteFriendService
//                .updateNickname(userId, favoriteId, request);
//
//        log.info("닉네임 수정 완료 - 사용자 ID: {}, 즐겨찾기 ID: {}", userId, favoriteId);
//        return ResponseEntity.ok(ApiResponse.success("닉네임이 수정되었습니다.", response));
//    }
}
