package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.domain.caresubject.*;
import carehub.domain.guardian.Guardian;
import carehub.web.dto.caresubject.CareSubjectCreateRequest;
import carehub.web.dto.caresubject.CareSubjectResponse;
import carehub.web.dto.caresubject.CareSubjectUpdateRequest;
import carehub.web.dto.guardian.GuardianResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/care-subjects")
@RequiredArgsConstructor
public class CareSubjectController extends BaseController {

    private final CareSubjectService careSubjectService;

    /**
     * 케어 대상 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CareSubjectResponse>> createCareSubject(
            @Valid @RequestBody CareSubjectCreateRequest request) {

        Long userId = getCurrentUserId();
        CareSubject careSubject = careSubjectService.createCareSubject(request, userId);
        CareSubjectResponse response = CareSubjectResponse.from(careSubject);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내가 접근 가능한 케어 대상 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CareSubjectResponse>>> getMyCareSubjects() {
        Long userId = getCurrentUserId();
        List<CareSubject> careSubjects = careSubjectService.getAccessibleCareSubjects(userId);
        List<CareSubjectResponse> responses = careSubjects.stream()
                .map(CareSubjectResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 케어 대상 상세 조회
     */
    @GetMapping("/{careSubjectId}")
    public ResponseEntity<ApiResponse<CareSubjectResponse>> getCareSubject(
            @PathVariable Long careSubjectId) {

        Long userId = getCurrentUserId();
        CareSubject careSubject = careSubjectService.getCareSubject(careSubjectId, userId);
        CareSubjectResponse response = CareSubjectResponse.from(careSubject);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 케어 대상 정보 수정
     */
    @PutMapping("/{careSubjectId}")
    public ResponseEntity<ApiResponse<CareSubjectResponse>> updateCareSubject(
            @PathVariable Long careSubjectId,
            @Valid @RequestBody CareSubjectUpdateRequest request) {

        Long userId = getCurrentUserId();
        CareSubject careSubject = careSubjectService.updateCareSubject(careSubjectId, request, userId);
        CareSubjectResponse response = CareSubjectResponse.from(careSubject);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 케어 대상 삭제 (비활성화)
     */
    @DeleteMapping("/{careSubjectId}")
    public ResponseEntity<ApiResponse<Void>> deleteCareSubject(@PathVariable Long careSubjectId) {
        Long userId = getCurrentUserId();
        careSubjectService.deleteCareSubject(careSubjectId, userId);

        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 케어 대상의 보호자 목록 조회
     */
    @GetMapping("/{careSubjectId}/guardians")
    public ResponseEntity<ApiResponse<List<GuardianResponse>>> getCareSubjectGuardians(
            @PathVariable Long careSubjectId) {

        Long userId = getCurrentUserId();
        List<Guardian> guardians = careSubjectService.getCareSubjectGuardians(careSubjectId, userId);
        List<GuardianResponse> responses = guardians.stream()
                .map(GuardianResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 내 보호자 관계 목록 조회
     */
    @GetMapping("/my-guardianships")
    public ResponseEntity<ApiResponse<List<GuardianResponse>>> getMyGuardianships() {
        Long userId = getCurrentUserId();
        List<Guardian> guardians = careSubjectService.getUserGuardianships(userId);
        List<GuardianResponse> responses = guardians.stream()
                .map(GuardianResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 내 대기 중인 초대 목록 조회
     */
    @GetMapping("/pending-invitations")
    public ResponseEntity<ApiResponse<List<GuardianResponse>>> getPendingInvitations() {
        Long userId = getCurrentUserId();
        List<Guardian> guardians = careSubjectService.getPendingInvitations(userId);
        List<GuardianResponse> responses = guardians.stream()
                .map(GuardianResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}