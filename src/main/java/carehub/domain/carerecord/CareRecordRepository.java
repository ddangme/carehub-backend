package carehub.domain.carerecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CareRecordRepository extends JpaRepository<CareRecord, Long> {

    /**
     * 특정 케어 대상의 기록 조회 (페이지네이션)
     */
    @Query("SELECT cr FROM CareRecord cr " +
            "LEFT JOIN FETCH cr.careSubject cs " +
            "LEFT JOIN FETCH cr.recordedBy rb " +
            "WHERE cs.id = :careSubjectId")
    Page<CareRecord> findByCareSubjectId(@Param("careSubjectId") Long careSubjectId, Pageable pageable);

    /**
     * 특정 케어 대상과 기록 유형으로 조회 (페이지네이션)
     */
    @Query("SELECT cr FROM CareRecord cr " +
            "LEFT JOIN FETCH cr.careSubject cs " +
            "LEFT JOIN FETCH cr.recordedBy rb " +
            "WHERE cs.id = :careSubjectId AND cr.recordType = :recordType")
    Page<CareRecord> findByCareSubjectIdAndRecordType(
            @Param("careSubjectId") Long careSubjectId,
            @Param("recordType") CareRecordType recordType,
            Pageable pageable);

    /**
     * 특정 케어 대상의 기간별 기록 조회 (페이지네이션)
     */
    @Query("SELECT cr FROM CareRecord cr " +
            "LEFT JOIN FETCH cr.careSubject cs " +
            "LEFT JOIN FETCH cr.recordedBy rb " +
            "WHERE cs.id = :careSubjectId " +
            "AND cr.recordedAt >= :startDate " +
            "AND cr.recordedAt <= :endDate")
    Page<CareRecord> findByCareSubjectIdAndRecordedAtBetween(
            @Param("careSubjectId") Long careSubjectId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 특정 케어 대상의 기간별 및 유형별 기록 조회 (페이지네이션)
     */
    @Query("SELECT cr FROM CareRecord cr " +
            "LEFT JOIN FETCH cr.careSubject cs " +
            "LEFT JOIN FETCH cr.recordedBy rb " +
            "WHERE cs.id = :careSubjectId " +
            "AND cr.recordType = :recordType " +
            "AND cr.recordedAt >= :startDate " +
            "AND cr.recordedAt <= :endDate")
    Page<CareRecord> findByCareSubjectIdAndRecordTypeAndRecordedAtBetween(
            @Param("careSubjectId") Long careSubjectId,
            @Param("recordType") CareRecordType recordType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * ID와 케어 대상으로 단일 기록 조회 (권한 체크용)
     */
    @Query("SELECT cr FROM CareRecord cr " +
            "LEFT JOIN FETCH cr.careSubject cs " +
            "LEFT JOIN FETCH cr.recordedBy rb " +
            "WHERE cr.id = :id AND cs.id = :careSubjectId")
    Optional<CareRecord> findByIdAndCareSubjectId(
            @Param("id") Long id,
            @Param("careSubjectId") Long careSubjectId);

    /**
     * 최근 기록 조회 (대시보드용)
     */
    @Query("SELECT cr FROM CareRecord cr " +
            "LEFT JOIN FETCH cr.careSubject cs " +
            "LEFT JOIN FETCH cr.recordedBy rb " +
            "WHERE cs.id = :careSubjectId " +
            "ORDER BY cr.recordedAt DESC")
    List<CareRecord> findRecentByCareSubjectId(@Param("careSubjectId") Long careSubjectId, Pageable pageable);

    /**
     * 사용자가 접근 가능한 기록인지 확인
     */
    @Query("SELECT COUNT(cr) > 0 FROM CareRecord cr " +
            "JOIN cr.careSubject cs " +
            "LEFT JOIN cs.guardians g " +
            "WHERE cr.id = :recordId " +
            "AND (cs.createdBy.id = :userId OR (g.user.id = :userId AND g.status = 'ACCEPTED'))")
    boolean isAccessibleByUser(@Param("recordId") Long recordId, @Param("userId") Long userId);
}
