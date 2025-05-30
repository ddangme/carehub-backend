package carehub.domain.guardian;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    /**
     * 특정 케어 대상의 모든 보호자 조회
     */
    List<Guardian> findByCareSubjectId(Long careSubjectId);

    /**
     * 특정 케어 대상의 승인된 보호자 조회
     */
    @Query("SELECT g FROM Guardian g WHERE g.careSubject.id = :careSubjectId AND g.status = 'ACCEPTED'")
    List<Guardian> findAcceptedByCareSubjectId(@Param("careSubjectId") Long careSubjectId);

    /**
     * 특정 사용자의 보호자 관계 조회
     */
    List<Guardian> findByUserId(Long userId);

    /**
     * 특정 사용자의 승인된 보호자 관계 조회
     */
    @Query("SELECT g FROM Guardian g WHERE g.user.id = :userId AND g.status = 'ACCEPTED'")
    List<Guardian> findAcceptedByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자의 대기 중인 초대 조회
     */
    @Query("SELECT g FROM Guardian g WHERE g.user.id = :userId AND g.status = 'PENDING'")
    List<Guardian> findPendingByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자와 케어 대상의 보호자 관계 조회
     */
    Optional<Guardian> findByUserIdAndCareSubjectId(Long userId, Long careSubjectId);

    /**
     * 특정 케어 대상의 PRIMARY 보호자 조회
     */
    @Query("SELECT g FROM Guardian g WHERE g.careSubject.id = :careSubjectId AND g.role = 'PRIMARY' AND g.status = 'ACCEPTED'")
    Optional<Guardian> findPrimaryByCareSubjectId(@Param("careSubjectId") Long careSubjectId);

    /**
     * 특정 사용자가 PRIMARY 보호자인지 확인
     */
    @Query("SELECT COUNT(g) > 0 FROM Guardian g WHERE g.user.id = :userId AND g.careSubject.id = :careSubjectId AND g.role = 'PRIMARY' AND g.status = 'ACCEPTED'")
    boolean isPrimaryGuardian(@Param("userId") Long userId, @Param("careSubjectId") Long careSubjectId);

    /**
     * 특정 케어 대상에 대한 보호자 수 조회
     */
    @Query("SELECT COUNT(g) FROM Guardian g WHERE g.careSubject.id = :careSubjectId AND g.status = 'ACCEPTED'")
    long countAcceptedByCareSubjectId(@Param("careSubjectId") Long careSubjectId);
}
