package carehub.domain.caresubject;

import carehub.common.exception.BusinessException;
import carehub.common.exception.ErrorCode;
import carehub.domain.guardian.Guardian;
import carehub.domain.guardian.GuardianRepository;
import carehub.domain.guardian.GuardianRole;
import carehub.domain.guardian.GuardianStatus;
import carehub.domain.guardian.Permission;
import carehub.domain.user.User;
import carehub.domain.user.UserRepository;
import carehub.web.dto.caresubject.CareSubjectCreateRequest;
import carehub.web.dto.caresubject.CareSubjectUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareSubjectService {

    private final CareSubjectRepository careSubjectRepository;
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;

    /**
     * 케어 대상 생성
     */
    @Transactional
    public CareSubject createCareSubject(CareSubjectCreateRequest request, Long createdById) {
        log.info("케어 대상 생성 시작 - 생성자 ID: {}, 요청: {}", createdById, request);

        // 요청 데이터 정리 및 검증
        request.cleanEmptyFields();
        request.validateRequest();

        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // BigDecimal 변환 (기존 호환성 유지)
        BigDecimal birthWeight = request.getBirthWeightGrams() != null
                ? new BigDecimal(request.getBirthWeightGrams()).divide(new BigDecimal("1000"))
                : null;
        BigDecimal birthHeight = request.getBirthHeightCm() != null
                ? new BigDecimal(request.getBirthHeightCm()).divide(new BigDecimal("100"))
                : null;

        CareSubject careSubject = CareSubject.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .bloodType(request.getBloodType())
                .birthWeight(birthWeight)
                .birthHeight(birthHeight)
                .profileImageUrl(request.getProfileImageUrl())
                .additionalInfo(request.getAdditionalInfo())
                .createdBy(creator)
                .mainCaregiver(creator) // 임시: createdBy와 동일하게 설정
                // 신생아 전용 필드들
                .birthWeightGrams(request.getBirthWeightGrams())
                .birthHeightCm(request.getBirthHeightCm())
                .headCircumferenceCm(request.getHeadCircumferenceCm())
                .gestationalAgeWeeks(request.getGestationalAgeWeeks())
                .deliveryType(request.getDeliveryType())
                .allergies(request.getAllergies())
                .specialCareNeeds(request.getSpecialCareNeeds())
                .lastCheckupDate(request.getLastCheckupDate())
                .build();

        // 케어 대상 유형 자동 결정
        careSubject.determineSubjectType();

        CareSubject savedCareSubject = careSubjectRepository.save(careSubject);
        log.info("케어 대상 저장 완료 - ID: {}, 이름: {}, 타입: {}",
                savedCareSubject.getId(), savedCareSubject.getName(), savedCareSubject.getSubjectType());

        // 생성자를 PRIMARY 보호자로 자동 등록
        Guardian primaryGuardian = Guardian.builder()
                .user(creator)
                .careSubject(savedCareSubject)
                .role(GuardianRole.PRIMARY)
                .status(GuardianStatus.ACCEPTED)
                .invitedBy(creator)
                .memo("케어 대상 생성자")
                .build();

        primaryGuardian.setDefaultPermissions();
        guardianRepository.save(primaryGuardian);
        log.info("PRIMARY 보호자 등록 완료 - 사용자: {}", creator.getEmail());

        return savedCareSubject;
    }

    /**
     * 특정 사용자가 접근 가능한 케어 대상 목록 조회
     */
    public List<CareSubject> getAccessibleCareSubjects(Long userId) {
        return careSubjectRepository.findAccessibleByUserId(userId);
    }

    /**
     * 케어 대상 상세 조회
     */
    public CareSubject getCareSubject(Long careSubjectId, Long userId) {
        return careSubjectRepository.findByIdAndAccessibleByUserId(careSubjectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "케어 대상을 찾을 수 없거나 접근 권한이 없습니다."));
    }

    /**
     * 케어 대상 정보 수정
     */
    @Transactional
    public CareSubject updateCareSubject(Long careSubjectId, CareSubjectUpdateRequest request, Long userId) {
        CareSubject careSubject = getCareSubject(careSubjectId, userId);

        // 케어 대상 수정 권한 확인
        Guardian userGuardian = guardianRepository.findByUserIdAndCareSubjectId(userId, careSubjectId)
                .orElse(null);

        // 생성자이거나 UPDATE_CARE_SUBJECT 권한이 있어야 함
        boolean isCreator = careSubject.getCreatedBy().getId().equals(userId);
        boolean hasUpdatePermission = userGuardian != null &&
                userGuardian.hasPermission(Permission.UPDATE_CARE_SUBJECT);

        if (!isCreator && !hasUpdatePermission) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "케어 대상 정보 수정 권한이 없습니다.");
        }

        // 정보 업데이트
        if (request.getName() != null) {
            careSubject.setName(request.getName());
        }
        if (request.getBloodType() != null) {
            careSubject.setBloodType(request.getBloodType());
        }
        if (request.getBirthWeight() != null) {
            careSubject.setBirthWeight(request.getBirthWeight());
        }
        if (request.getBirthHeight() != null) {
            careSubject.setBirthHeight(request.getBirthHeight());
        }
        if (request.getProfileImageUrl() != null) {
            careSubject.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getAdditionalInfo() != null) {
            careSubject.setAdditionalInfo(request.getAdditionalInfo());
        }

        log.info("Updated care subject: {} by user: {}", careSubject.getName(), userId);
        return careSubjectRepository.save(careSubject);
    }

    /**
     * 케어 대상 삭제 (비활성화)
     */
    @Transactional
    public void deleteCareSubject(Long careSubjectId, Long userId) {
        CareSubject careSubject = getCareSubject(careSubjectId, userId);

        // 케어 대상 삭제 권한 확인
        Guardian userGuardian = guardianRepository.findByUserIdAndCareSubjectId(userId, careSubjectId)
                .orElse(null);

        // 생성자이거나 DELETE_CARE_SUBJECT 권한이 있어야 함
        boolean isCreator = careSubject.getCreatedBy().getId().equals(userId);
        boolean hasDeletePermission = userGuardian != null &&
                userGuardian.hasPermission(Permission.DELETE_CARE_SUBJECT);

        if (!isCreator && !hasDeletePermission) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "케어 대상 삭제 권한이 없습니다.");
        }

        careSubject.setIsActive(false);
        careSubjectRepository.save(careSubject);

        log.info("Deactivated care subject: {} by user: {}", careSubject.getName(), userId);
    }

    /**
     * 케어 대상의 보호자 목록 조회
     */
    public List<Guardian> getCareSubjectGuardians(Long careSubjectId, Long userId) {
        // 먼저 접근 권한 확인
        getCareSubject(careSubjectId, userId);

        return guardianRepository.findAcceptedByCareSubjectId(careSubjectId);
    }

    /**
     * 특정 사용자의 보호자 관계 조회
     */
    public List<Guardian> getUserGuardianships(Long userId) {
        return guardianRepository.findAcceptedByUserId(userId);
    }

    /**
     * 특정 사용자의 대기 중인 초대 조회
     */
    public List<Guardian> getPendingInvitations(Long userId) {
        return guardianRepository.findPendingByUserId(userId);
    }
}
