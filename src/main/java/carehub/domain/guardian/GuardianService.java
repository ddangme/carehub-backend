package carehub.domain.guardian;

import carehub.common.exception.BusinessException;
import carehub.common.exception.ErrorCode;
import carehub.domain.caresubject.CareSubject;
import carehub.domain.caresubject.CareSubjectRepository;
import carehub.domain.user.User;
import carehub.domain.user.UserRepository;
import carehub.web.dto.guardian.GuardianInviteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final CareSubjectRepository careSubjectRepository;
    private final UserRepository userRepository;

    /**
     * 보호자 초대
     */
    @Transactional
    public Guardian inviteGuardian(GuardianInviteRequest request, Long inviterId) {
        // 케어 대상 조회
        CareSubject careSubject = careSubjectRepository.findByIdAndAccessibleByUserId(
                        request.getCareSubjectId(), inviterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "케어 대상을 찾을 수 없거나 접근 권한이 없습니다."));

        // 초대자 권한 확인 (PRIMARY 또는 SECONDARY만 초대 가능)
        Guardian inviterGuardian = guardianRepository.findByUserIdAndCareSubjectId(inviterId, request.getCareSubjectId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED, "보호자 초대 권한이 없습니다."));

        if (!inviterGuardian.hasPermission(Permission.MANAGE_GUARDIANS)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "보호자 초대 권한이 없습니다.");
        }

        // 초대받을 사용자 조회
        User invitee = userRepository.findByEmail(request.getInviteeEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "초대할 사용자를 찾을 수 없습니다."));

        // 이미 보호자 관계가 있는지 확인
        if (guardianRepository.findByUserIdAndCareSubjectId(invitee.getId(), request.getCareSubjectId()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 보호자로 등록되어 있습니다.");
        }

        // PRIMARY 보호자는 하나만 가능
        if (request.getRole() == GuardianRole.PRIMARY) {
            if (guardianRepository.findPrimaryByCareSubjectId(request.getCareSubjectId()).isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "주 보호자는 한 명만 등록 가능합니다.");
            }
        }

        // 보호자 초대 생성
        Guardian guardian = Guardian.builder()
                .user(invitee)
                .careSubject(careSubject)
                .role(request.getRole())
                .status(GuardianStatus.PENDING)
                .invitedBy(inviterGuardian.getUser())
                .memo(request.getMemo())
                .build();

        guardian.setDefaultPermissions();
        Guardian savedGuardian = guardianRepository.save(guardian);

        // TODO: 이메일 알림 발송
        log.info("Guardian invitation created: {} invited {} for care subject: {}",
                inviterGuardian.getUser().getEmail(), invitee.getEmail(), careSubject.getName());

        return savedGuardian;
    }

    /**
     * 보호자 초대 수락
     */
    @Transactional
    public Guardian acceptInvitation(Long guardianId, Long userId) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "초대를 찾을 수 없습니다."));

        // 본인의 초대인지 확인
        if (!guardian.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "본인의 초대만 처리할 수 있습니다.");
        }

        // 대기 상태인지 확인
        if (guardian.getStatus() != GuardianStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 처리된 초대입니다.");
        }

        guardian.acceptInvitation();
        Guardian savedGuardian = guardianRepository.save(guardian);

        log.info("Guardian invitation accepted: {} for care subject: {}",
                guardian.getUser().getEmail(), guardian.getCareSubject().getName());

        return savedGuardian;
    }

    /**
     * 보호자 초대 거절
     */
    @Transactional
    public Guardian rejectInvitation(Long guardianId, Long userId) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "초대를 찾을 수 없습니다."));

        // 본인의 초대인지 확인
        if (!guardian.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "본인의 초대만 처리할 수 있습니다.");
        }

        // 대기 상태인지 확인
        if (guardian.getStatus() != GuardianStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 처리된 초대입니다.");
        }

        guardian.rejectInvitation();
        Guardian savedGuardian = guardianRepository.save(guardian);

        log.info("Guardian invitation rejected: {} for care subject: {}",
                guardian.getUser().getEmail(), guardian.getCareSubject().getName());

        return savedGuardian;
    }

    /**
     * 보호자 권한 수정
     */
    @Transactional
    public Guardian updateGuardianRole(Long guardianId, GuardianRole newRole, Long userId) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "보호자를 찾을 수 없습니다."));

        // 권한 확인 (PRIMARY 보호자만 권한 수정 가능)
        if (!guardianRepository.isPrimaryGuardian(userId, guardian.getCareSubject().getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "보호자 권한 수정 권한이 없습니다.");
        }

        // PRIMARY 보호자는 하나만 가능
        if (newRole == GuardianRole.PRIMARY && guardian.getRole() != GuardianRole.PRIMARY) {
            if (guardianRepository.findPrimaryByCareSubjectId(guardian.getCareSubject().getId()).isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "주 보호자는 한 명만 등록 가능합니다.");
            }
        }

        guardian.setRole(newRole);
        guardian.setDefaultPermissions();
        Guardian savedGuardian = guardianRepository.save(guardian);

        log.info("Guardian role updated: {} to {} for care subject: {}",
                guardian.getUser().getEmail(), newRole, guardian.getCareSubject().getName());

        return savedGuardian;
    }

    /**
     * 보호자 제거
     */
    @Transactional
    public void removeGuardian(Long guardianId, Long userId) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "보호자를 찾을 수 없습니다."));

        // 권한 확인 (PRIMARY 보호자만 제거 가능, 또는 본인)
        boolean isPrimary = guardianRepository.isPrimaryGuardian(userId, guardian.getCareSubject().getId());
        boolean isSelf = guardian.getUser().getId().equals(userId);

        if (!isPrimary && !isSelf) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "보호자 제거 권한이 없습니다.");
        }

        // PRIMARY 보호자는 자신을 제거할 수 없음 (다른 PRIMARY가 있을 때만 가능)
        if (guardian.getRole() == GuardianRole.PRIMARY && isSelf) {
            long primaryCount = guardianRepository.countAcceptedByCareSubjectId(guardian.getCareSubject().getId());
            if (primaryCount <= 1) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최소 한 명의 주 보호자가 필요합니다.");
            }
        }

        guardianRepository.delete(guardian);

        log.info("Guardian removed: {} from care subject: {}",
                guardian.getUser().getEmail(), guardian.getCareSubject().getName());
    }

    /**
     * 특정 케어 대상의 보호자 목록 조회
     */
    public List<Guardian> getGuardiansByCareSubject(Long careSubjectId, Long userId) {
        // 접근 권한 확인
        careSubjectRepository.findByIdAndAccessibleByUserId(careSubjectId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "케어 대상을 찾을 수 없거나 접근 권한이 없습니다."));

        return guardianRepository.findAcceptedByCareSubjectId(careSubjectId);
    }
}
