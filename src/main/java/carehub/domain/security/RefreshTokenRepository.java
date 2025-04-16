package carehub.domain.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId);

    @Transactional
    void deleteAllByUserId(Long userId);

    @Transactional
    void deleteByUserIdAndDeviceId(Long userId, String deviceId);

    @Transactional
    void deleteByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < ?1")
    void deleteAllExpiredTokens(LocalDateTime now);
}