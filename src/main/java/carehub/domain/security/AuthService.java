package carehub.domain.security;

import carehub.common.exception.BusinessException;
import carehub.common.exception.ErrorCode;
import carehub.domain.user.User;
import carehub.domain.user.UserRepository;
import carehub.web.dto.auth.AuthRequestResponse.TokenResponse;
import carehub.web.dto.auth.AuthRequestResponse.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * 로그인 및 토큰 발급
     */
    @Transactional
    public TokenResponse login(String email, String password, String deviceId, String fcmToken) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (deviceId == null || deviceId.isBlank()) {
            deviceId = UUID.randomUUID().toString();
        }

        refreshTokenRepository.findByUserIdAndDeviceId(user.getId(), deviceId)
                .ifPresent(refreshTokenRepository::delete);

        String accessToken = tokenProvider.createAccessToken(email);
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), deviceId);

        if (fcmToken != null && !fcmToken.isBlank()) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> {
                        token.setFcmToken(fcmToken);
                        refreshTokenRepository.save(token);
                    });
        }

        user.getPreference().setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .tokenType("Bearer")
                .userInfo(mapUserInfo(user))
                .build();
    }

    /**
     * 소셜 로그인 사용자를 위한 토큰 생성 (인증 과정 없이 직접 토큰 생성)
     */
    @Transactional
    public TokenResponse createTokensForSocialLogin(User user, String deviceId, String fcmToken) {
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = UUID.randomUUID().toString();
        }

        refreshTokenRepository.findByUserIdAndDeviceId(user.getId(), deviceId)
                .ifPresent(refreshTokenRepository::delete);

        String accessToken = tokenProvider.createAccessToken(user.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), deviceId);

        if (fcmToken != null && !fcmToken.isBlank()) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> {
                        token.setFcmToken(fcmToken);
                        refreshTokenRepository.save(token);
                    });
        }

        user.getPreference().setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .tokenType("Bearer")
                .userInfo(mapUserInfo(user))
                .build();
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public TokenResponse refreshToken(String refreshToken, String deviceId) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (deviceId != null && !deviceId.isBlank() && !deviceId.equals(storedToken.getDeviceId())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = tokenProvider.createAccessToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .tokenType("Bearer")
                .userInfo(mapUserInfo(user))
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String accessToken, String refreshToken, Long userId, String deviceId) {
        if (accessToken != null && !accessToken.isBlank()) {
            tokenProvider.blacklistToken(accessToken);
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }

        else if (userId != null && deviceId != null && !deviceId.isBlank()) {
            refreshTokenRepository.deleteByUserIdAndDeviceId(userId, deviceId);
        }
    }

    /**
     * 모든 기기에서 로그아웃
     */
    @Transactional
    public void logoutFromAllDevices(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    private UserInfo mapUserInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}