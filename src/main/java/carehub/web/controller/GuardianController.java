package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.domain.guardian.*;
import carehub.web.dto.guardian.GuardianInviteRequest;
import carehub.web.dto.guardian.GuardianResponse;
import carehub.web.dto.guardian.GuardianRoleUpdateRequest;
import carehub.web.dto.guardian.InvitationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/guardians")
@RequiredArgsConstructor
public class GuardianController extends BaseController {

    private final GuardianService guardianService;

    /**
     * 보호자 초대
     */
    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<GuardianResponse>> inviteGuardian(
            @Valid @RequestBody GuardianInviteRequest request) {

        Long userId = getCurrentUserId();
        Guardian guardian = guardianService.inviteGuardian(request, userId);
        GuardianResponse response = GuardianResponse.from(guardian);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 보호자 초대 수락
     */
    @PostMapping("/{guardianId}/accept")
    public ResponseEntity<ApiResponse<InvitationResponse>> acceptInvitation(
            @PathVariable Long guardianId) {

        Long userId = getCurrentUserId();
        Guardian guardian = guardianService.acceptInvitation(guardianId, userId);
        InvitationResponse response = InvitationResponse.accepted(guardian.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 보호자 초대 거절
     */
    @PostMapping("/{guardianId}/reject")
    public ResponseEntity<ApiResponse<InvitationResponse>> rejectInvitation(
            @PathVariable Long guardianId) {

        Long userId = getCurrentUserId();
        Guardian guardian = guardianService.rejectInvitation(guardianId, userId);
        InvitationResponse response = InvitationResponse.rejected(guardian.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 보호자 권한 수정
     */
    @PutMapping("/{guardianId}/role")
    public ResponseEntity<ApiResponse<GuardianResponse>> updateGuardianRole(
            @PathVariable Long guardianId,
            @Valid @RequestBody GuardianRoleUpdateRequest request) {

        Long userId = getCurrentUserId();
        Guardian guardian = guardianService.updateGuardianRole(guardianId, request.getNewRole(), userId);
        GuardianResponse response = GuardianResponse.from(guardian);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 보호자 제거
     */
    @DeleteMapping("/{guardianId}")
    public ResponseEntity<ApiResponse<Void>> removeGuardian(@PathVariable Long guardianId) {
        Long userId = getCurrentUserId();
        guardianService.removeGuardian(guardianId, userId);

        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 특정 케어 대상의 보호자 목록 조회
     */
    @GetMapping("/care-subject/{careSubjectId}")
    public ResponseEntity<ApiResponse<List<GuardianResponse>>> getGuardiansByCareSubject(
            @PathVariable Long careSubjectId) {

        Long userId = getCurrentUserId();
        List<Guardian> guardians = guardianService.getGuardiansByCareSubject(careSubjectId, userId);
        List<GuardianResponse> responses = guardians.stream()
                .map(GuardianResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}