package carehub.domain.carerecord;

import carehub.common.dto.response.PageResponse;
import carehub.common.exception.BusinessException;
import carehub.common.exception.ErrorCode;
import carehub.domain.caresubject.CareSubject;
import carehub.domain.caresubject.CareSubjectRepository;
import carehub.domain.user.User;
import carehub.domain.user.UserRepository;
import carehub.web.dto.carerecord.CareRecordCreateRequest;
import carehub.web.dto.carerecord.CareRecordResponse;
import carehub.web.dto.carerecord.CareRecordUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareRecordService {

    private final CareRecordRepository careRecordRepository;
    private final CareSubjectRepository careSubjectRepository;
    private final UserRepository userRepository;

    /**
     * 케어 기록 생성
     */
    @Transactional
    public CareRecord createCareRecord(CareRecordCreateRequest request, Long userId) {
        log.info("케어 기록 생성 시작 - 사용자 ID: {}, 케어 대상 ID: {}", userId, request.getCareSubjectId());

        // 케어 대상 조회 및 접근 권한 확인
        CareSubject careSubject = careSubjectRepository.findByIdAndAccessibleByUserId(
                        request.getCareSubjectId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "케어 대상을 찾을 수 없거나 접근 권한이 없습니다."));

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 케어 기록 생성
        CareRecord careRecord = CareRecord.builder()
                .careSubject(careSubject)
                .recordType(request.getRecordType())
                .title(request.getTitle())
                .description(request.getDescription())
                .recordedAt(request.getRecordedAt())
                .recordData(request.getRecordData())
                .recordedBy(user)
                .build();

        CareRecord savedRecord = careRecordRepository.save(careRecord);
        log.info("케어 기록 생성 완료 - ID: {}, 제목: {}", savedRecord.getId(), savedRecord.getTitle());

        return savedRecord;
    }

    /**
     * 케어 기록 목록 조회 (페이지네이션)
     */
    public PageResponse<CareRecordResponse> getCareRecords(
            Long careSubjectId,
            CareRecordType recordType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            Long userId) {

        // 케어 대상 접근 권한 확인
        careSubjectRepository.findByIdAndAccessibleByUserId(careSubjectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "케어 대상을 찾을 수 없거나 접근 권한이 없습니다."));

        // 정렬 설정 (최신순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "recordedAt"));

        Page<CareRecord> recordPage;

        // 조건에 따른 쿼리 실행
        if (recordType != null && startDate != null && endDate != null) {
            recordPage = careRecordRepository.findByCareSubjectIdAndRecordTypeAndRecordedAtBetween(
                    careSubjectId, recordType, startDate, endDate, pageable);
        } else if (recordType != null) {
            recordPage = careRecordRepository.findByCareSubjectIdAndRecordType(
                    careSubjectId, recordType, pageable);
        } else if (startDate != null && endDate != null) {
            recordPage = careRecordRepository.findByCareSubjectIdAndRecordedAtBetween(
                    careSubjectId, startDate, endDate, pageable);
        } else {
            recordPage = careRecordRepository.findByCareSubjectId(careSubjectId, pageable);
        }

        // 응답 변환
        List<CareRecordResponse> responseList = recordPage.getContent().stream()
                .map(CareRecordResponse::from)
                .toList();

        return PageResponse.<CareRecordResponse>builder()
                .content(responseList)
                .totalElements(recordPage.getTotalElements())
                .totalPages(recordPage.getTotalPages())
                .size(recordPage.getSize())
                .number(recordPage.getNumber())
                .first(recordPage.isFirst())
                .last(recordPage.isLast())
                .build();
    }

    /**
     * 케어 기록 상세 조회
     */
    public CareRecord getCareRecord(Long recordId, Long userId) {
        // 접근 권한 확인
        if (!careRecordRepository.isAccessibleByUser(recordId, userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "케어 기록에 접근할 권한이 없습니다.");
        }

        return careRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "케어 기록을 찾을 수 없습니다."));
    }

    /**
     * 케어 기록 수정
     */
    @Transactional
    public CareRecord updateCareRecord(Long recordId, CareRecordUpdateRequest request, Long userId) {
        // 기록 조회 및 권한 확인
        CareRecord careRecord = getCareRecord(recordId, userId);

        // 작성자만 수정 가능
        if (!careRecord.getRecordedBy().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "본인이 작성한 기록만 수정할 수 있습니다.");
        }

        // 수정 가능한 필드 업데이트
        if (request.getTitle() != null) {
            careRecord.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            careRecord.setDescription(request.getDescription());
        }
        if (request.getRecordedAt() != null) {
            careRecord.setRecordedAt(request.getRecordedAt());
        }
        if (request.getRecordData() != null) {
            careRecord.setRecordData(request.getRecordData());
        }

        CareRecord updatedRecord = careRecordRepository.save(careRecord);
        log.info("케어 기록 수정 완료 - ID: {}, 사용자: {}", recordId, userId);

        return updatedRecord;
    }

    /**
     * 케어 기록 삭제
     */
    @Transactional
    public void deleteCareRecord(Long recordId, Long userId) {
        // 기록 조회 및 권한 확인
        CareRecord careRecord = getCareRecord(recordId, userId);

        // 작성자만 삭제 가능
        if (!careRecord.getRecordedBy().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "본인이 작성한 기록만 삭제할 수 있습니다.");
        }

        careRecordRepository.delete(careRecord);
        log.info("케어 기록 삭제 완료 - ID: {}, 사용자: {}", recordId, userId);
    }

    /**
     * 최근 케어 기록 조회 (대시보드용)
     */
    public List<CareRecordResponse> getRecentCareRecords(Long careSubjectId, int limit, Long userId) {
        // 케어 대상 접근 권한 확인
        careSubjectRepository.findByIdAndAccessibleByUserId(careSubjectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "케어 대상을 찾을 수 없거나 접근 권한이 없습니다."));

        Pageable pageable = PageRequest.of(0, limit);
        List<CareRecord> records = careRecordRepository.findRecentByCareSubjectId(careSubjectId, pageable);

        return records.stream()
                .map(CareRecordResponse::from)
                .toList();
    }
}
