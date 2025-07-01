package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.common.dto.response.PageResponse;
import carehub.domain.carerecord.CareRecord;
import carehub.domain.carerecord.CareRecordService;
import carehub.domain.carerecord.CareRecordType;
import carehub.web.dto.carerecord.CareRecordCreateRequest;
import carehub.web.dto.carerecord.CareRecordResponse;
import carehub.web.dto.carerecord.CareRecordUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/care-records")
@RequiredArgsConstructor
public class CareRecordController extends BaseController {

    private final CareRecordService careRecordService;

    /**
     * 케어 기록 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CareRecordResponse>> createCareRecord(
            @Valid @RequestBody CareRecordCreateRequest request) {

        Long userId = getCurrentUserId();
        CareRecord careRecord = careRecordService.createCareRecord(request, userId);
        CareRecordResponse response = CareRecordResponse.from(careRecord);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 케어 기록 목록 조회 (페이지네이션)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CareRecordResponse>>> getCareRecords(
            @RequestParam Long careSubjectId,
            @RequestParam(required = false) CareRecordType recordType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getCurrentUserId();
        PageResponse<CareRecordResponse> response = careRecordService.getCareRecords(
                careSubjectId, recordType, startDate, endDate, page, size, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 케어 기록 상세 조회
     */
    @GetMapping("/{recordId}")
    public ResponseEntity<ApiResponse<CareRecordResponse>> getCareRecord(
            @PathVariable Long recordId) {

        Long userId = getCurrentUserId();
        CareRecord careRecord = careRecordService.getCareRecord(recordId, userId);
        CareRecordResponse response = CareRecordResponse.from(careRecord);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 케어 기록 수정
     */
    @PutMapping("/{recordId}")
    public ResponseEntity<ApiResponse<CareRecordResponse>> updateCareRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody CareRecordUpdateRequest request) {

        Long userId = getCurrentUserId();
        CareRecord careRecord = careRecordService.updateCareRecord(recordId, request, userId);
        CareRecordResponse response = CareRecordResponse.from(careRecord);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 케어 기록 삭제
     */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Void>> deleteCareRecord(@PathVariable Long recordId) {
        Long userId = getCurrentUserId();
        careRecordService.deleteCareRecord(recordId, userId);

        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 최근 케어 기록 조회 (대시보드용)
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<CareRecordResponse>>> getRecentCareRecords(
            @RequestParam Long careSubjectId,
            @RequestParam(defaultValue = "10") int limit) {

        Long userId = getCurrentUserId();
        List<CareRecordResponse> response = careRecordService.getRecentCareRecords(
                careSubjectId, limit, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
