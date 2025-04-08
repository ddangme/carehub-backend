package carehub.domain.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired refresh tokens...");
        LocalDateTime now = LocalDateTime.now();

        try {
            refreshTokenRepository.deleteAllExpiredTokens(now);
            log.info("Expired refresh tokens cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error cleaning up expired refresh tokens", e);
        }
    }
}