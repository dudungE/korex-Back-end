package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.RecipientRequest;
import com.project.korex.ForeignTransfer.dto.response.RecipientResponse;
import com.project.korex.ForeignTransfer.service.RecipientService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ForeignTransfer/recipients")
@RequiredArgsConstructor
public class RecipientController {

    private final RecipientService recipientService;

    @Operation(summary = "새 수취인 생성", description = "로그인한 사용자가 새로운 수취인을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "수취인 생성 성공",
                    content = @Content(schema = @Schema(implementation = RecipientResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<RecipientResponse> createRecipient(
            @RequestBody RecipientRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        RecipientResponse response = recipientService.createRecipient(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "수취인 단건 조회", description = "로그인한 사용자가 지정한 수취인 ID의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RecipientResponse.class))),
            @ApiResponse(responseCode = "404", description = "수취인 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/{recipientId}")
    public ResponseEntity<RecipientResponse> getRecipient(
            @Parameter(description = "조회할 수취인 ID") @PathVariable Long recipientId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        RecipientResponse response = recipientService.getRecipientById(userId, recipientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 전체 수취인 조회", description = "로그인한 사용자의 모든 수취인을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RecipientResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<List<RecipientResponse>> getRecipientsByUserId(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        List<RecipientResponse> recipients = recipientService.getRecipientsByUserId(userId);
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "활성화된 수취인 조회", description = "로그인한 사용자의 활성화된 수취인만 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RecipientResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/active")
    public ResponseEntity<List<RecipientResponse>> getActiveRecipients(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        List<RecipientResponse> recipients = recipientService.getActiveRecipientsByUserId(userId);
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "수취인 정보 수정", description = "로그인한 사용자가 수취인 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = RecipientResponse.class))),
            @ApiResponse(responseCode = "404", description = "수취인 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PutMapping("/{recipientId}")
    public ResponseEntity<RecipientResponse> updateRecipient(
            @Parameter(description = "수정할 수취인 ID") @PathVariable Long recipientId,
            @RequestBody RecipientRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        RecipientResponse response = recipientService.updateRecipient(userId, recipientId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "수취인 비활성화", description = "지정한 수취인을 비활성화 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비활성화 성공"),
            @ApiResponse(responseCode = "404", description = "수취인 없음")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateRecipient(
            @Parameter(description = "비활성화할 수취인 ID") @PathVariable Long id
    ) {
        recipientService.deactivateRecipient(id);
        return ResponseEntity.ok().build();
    }
}