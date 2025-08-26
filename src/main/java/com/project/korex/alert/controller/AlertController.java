package com.project.korex.alert.controller;

import com.project.korex.alert.dto.AlertCreateRequestDto;
import com.project.korex.alert.dto.AlertResponseDto;
import com.project.korex.alert.dto.AlertUpdateRequestDto;
import com.project.korex.alert.dto.ApiResponse;
import com.project.korex.alert.entity.AlertHistory;
import com.project.korex.alert.entity.AlertSetting;
import com.project.korex.alert.service.AlertService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;

    /**
     * Create new alert
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponseDto>> createAlert(
            @Valid @RequestBody AlertCreateRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Creating alert for user ID: {}, currency: {}, target rate: {}, condition: {}",
                    userId, request.getCurrencyCode(), request.getTargetRate(), request.getCondition());

            AlertSetting alert = alertService.createAlert(
                    userId,
                    request.getCurrencyCode(),
                    request.getTargetRate(),
                    request.getCondition()
            );

            AlertResponseDto responseDto = AlertResponseDto.from(alert);

            log.info("Alert created successfully - ID: {}, User ID: {}, Currency: {}",
                    alert.getId(), userId, request.getCurrencyCode());

            return ResponseEntity.ok(
                    ApiResponse.success(responseDto, "Alert created successfully")
            );

        } catch (IllegalArgumentException e) {
            log.warn("Failed to create alert - Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            log.error("Error occurred while creating alert for user ID: {}",
                    customUserPrincipal.getUser().getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while creating the alert")
            );
        }
    }

    /**
     * Get user's active alerts
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AlertResponseDto>>> getMyAlerts(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Fetching active alerts for user ID: {}", userId);

            List<AlertSetting> alerts = alertService.getUserAlerts(userId);
            List<AlertResponseDto> responseDtos = alerts.stream()
                    .map(AlertResponseDto::from)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} active alerts for user ID: {}",
                    responseDtos.size(), userId);

            return ResponseEntity.ok(
                    ApiResponse.success(responseDtos, "Active alerts retrieved successfully")
            );

        } catch (Exception e) {
            log.error("Error occurred while fetching alerts for user ID: {}",
                    customUserPrincipal.getUser().getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while retrieving alerts")
            );
        }
    }

    /**
     * Get all user alerts (including inactive)
     */
    @GetMapping("/my/all")
    public ResponseEntity<ApiResponse<List<AlertResponseDto>>> getAllMyAlerts(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Fetching all alerts for user ID: {}", userId);

            List<AlertSetting> alerts = alertService.getAllUserAlerts(userId);
            List<AlertResponseDto> responseDtos = alerts.stream()
                    .map(AlertResponseDto::from)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} total alerts for user ID: {}",
                    responseDtos.size(), userId);

            return ResponseEntity.ok(
                    ApiResponse.success(responseDtos, "All alerts retrieved successfully")
            );

        } catch (Exception e) {
            log.error("Error occurred while fetching all alerts for user ID: {}",
                    customUserPrincipal.getUser().getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while retrieving all alerts")
            );
        }
    }

    /**
     * Get specific alert details
     */
    @GetMapping("/{alertId}")
    public ResponseEntity<ApiResponse<AlertResponseDto>> getAlert(
            @PathVariable Long alertId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Fetching alert details - Alert ID: {}, User ID: {}", alertId, userId);

            AlertSetting alert = alertService.getAlertById(alertId, userId);
            AlertResponseDto responseDto = AlertResponseDto.from(alert);

            log.info("Successfully retrieved alert details - Alert ID: {}", alertId);

            return ResponseEntity.ok(
                    ApiResponse.success(responseDto, "Alert details retrieved successfully")
            );

        } catch (IllegalArgumentException e) {
            log.warn("Failed to retrieve alert - Alert ID: {}, Error: {}", alertId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            log.error("Error occurred while fetching alert details - Alert ID: {}", alertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while retrieving alert details")
            );
        }
    }

    /**
     * Update alert
     */
    @PutMapping("/{alertId}")
    public ResponseEntity<ApiResponse<AlertResponseDto>> updateAlert(
            @PathVariable Long alertId,
            @Valid @RequestBody AlertUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Updating alert - Alert ID: {}, User ID: {}, New target: {}, New condition: {}",
                    alertId, userId, request.getTargetRate(), request.getCondition());

            AlertSetting updatedAlert = alertService.updateAlert(
                    alertId, userId, request.getTargetRate(), request.getCondition()
            );

            AlertResponseDto responseDto = AlertResponseDto.from(updatedAlert);

            log.info("Alert updated successfully - Alert ID: {}, User ID: {}", alertId, userId);

            return ResponseEntity.ok(
                    ApiResponse.success(responseDto, "Alert updated successfully")
            );

        } catch (IllegalArgumentException e) {
            log.warn("Failed to update alert - Alert ID: {}, Error: {}", alertId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            log.error("Error occurred while updating alert - Alert ID: {}", alertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while updating the alert")
            );
        }
    }

    /**
     * Toggle alert activation status
     */
    @PatchMapping("/{alertId}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleAlert(
            @PathVariable Long alertId,
            @RequestParam boolean isActive,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Toggling alert status - Alert ID: {}, User ID: {}, New status: {}",
                    alertId, userId, isActive);

            alertService.toggleAlert(alertId, userId, isActive);

            String message = isActive ? "Alert activated successfully" : "Alert deactivated successfully";

            log.info("Alert status changed - Alert ID: {}, User ID: {}, Active: {}",
                    alertId, userId, isActive);

            return ResponseEntity.ok(
                    ApiResponse.success(null, message)
            );

        } catch (IllegalArgumentException e) {
            log.warn("Failed to toggle alert status - Alert ID: {}, Error: {}", alertId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            log.error("Error occurred while toggling alert status - Alert ID: {}", alertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while changing alert status")
            );
        }
    }

    /**
     * Delete alert
     */
    @DeleteMapping("/{alertId}")
    public ResponseEntity<ApiResponse<Void>> deleteAlert(
            @PathVariable Long alertId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Deleting alert - Alert ID: {}, User ID: {}", alertId, userId);

            alertService.deleteAlert(alertId, userId);

            log.info("Alert deleted successfully - Alert ID: {}, User ID: {}", alertId, userId);

            return ResponseEntity.ok(
                    ApiResponse.success(null, "Alert deleted successfully")
            );

        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete alert - Alert ID: {}, Error: {}", alertId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            log.error("Error occurred while deleting alert - Alert ID: {}", alertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while deleting the alert")
            );
        }
    }

    /**
     * Get user's alert history (paginated)
     */
    @GetMapping("/my/history")
    public ResponseEntity<ApiResponse<Page<AlertHistory>>> getMyAlertHistory(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Long userId = customUserPrincipal.getUser().getId();

            log.debug("Fetching alert history for user ID: {}, Page: {}, Size: {}",
                    userId, pageable.getPageNumber(), pageable.getPageSize());

            Page<AlertHistory> history = alertService.getUserAlertHistory(userId, pageable);

            log.info("Successfully retrieved alert history for user ID: {} - Total elements: {}",
                    userId, history.getTotalElements());

            return ResponseEntity.ok(
                    ApiResponse.success(history, "Alert history retrieved successfully")
            );

        } catch (Exception e) {
            log.error("Error occurred while fetching alert history for user ID: {}",
                    customUserPrincipal.getUser().getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("An error occurred while retrieving alert history")
            );
        }
    }


}
