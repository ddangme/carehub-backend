package carehub.domain.caresubject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CareSubjectRepository extends JpaRepository<CareSubject, Long> {

    /**
     * 특정 사용자가 생성한 케어 대상 목록 조회
     */
    List<CareSubject> findByCreatedByIdAndIsActiveTrue(Long createdById);

    /**
     * 특정 사용자가 보호자로 등록된 케어 대상 목록 조회 (승인된 것만)
     */
    @Query("SELECT DISTINCT cs FROM CareSubject cs " +
            "JOIN FETCH cs.guardians g " +
            "JOIN FETCH g.user " +
            "WHERE g.user.id = :userId AND g.status = 'ACCEPTED' AND cs.isActive = true")
    List<CareSubject> findByGuardianUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자가 접근 가능한 모든 케어 대상 조회 (생성자 + 보호자)
     * N+1 문제 방지를 위해 필요한 연관 엔티티들을 함께 로드
     */
    @Query("SELECT DISTINCT cs FROM CareSubject cs " +
            "LEFT JOIN FETCH cs.guardians g " +
            "LEFT JOIN FETCH g.user " +
            "LEFT JOIN FETCH cs.createdBy " +
            "WHERE (cs.createdBy.id = :userId OR (g.user.id = :userId AND g.status = 'ACCEPTED')) " +
            "AND cs.isActive = true")
    List<CareSubject> findAccessibleByUserId(@Param("userId") Long userId);

    /**
     * ID와 사용자 ID로 접근 가능한 케어 대상 조회
     * 상세 조회용으로 모든 필요한 연관 엔티티를 함께 로드
     */
    @Query("SELECT cs FROM CareSubject cs " +
            "LEFT JOIN FETCH cs.guardians g " +
            "LEFT JOIN FETCH g.user " +
            "LEFT JOIN FETCH cs.createdBy " +
            "WHERE cs.id = :careSubjectId " +
            "AND (cs.createdBy.id = :userId OR (g.user.id = :userId AND g.status = 'ACCEPTED')) " +
            "AND cs.isActive = true")
    Optional<CareSubject> findByIdAndAccessibleByUserId(@Param("careSubjectId") Long careSubjectId,
                                                        @Param("userId") Long userId);

    /**
     * 특정 사용자가 PRIMARY 보호자인 케어 대상 조회
     */
    @Query("SELECT DISTINCT cs FROM CareSubject cs " +
            "JOIN cs.guardians g " +
            "WHERE g.user.id = :userId AND g.role = 'PRIMARY' AND g.status = 'ACCEPTED' AND cs.isActive = true")
    List<CareSubject> findByPrimaryGuardianUserId(@Param("userId") Long userId);
}
