package carehub.domain.security.oauth2;

import carehub.common.exception.BusinessException;
import carehub.common.exception.ErrorCode;
import carehub.domain.security.AuthService;
import carehub.domain.security.JwtTokenProvider;
import carehub.domain.user.User;
import carehub.domain.user.UserPreference;
import carehub.domain.user.UserRepository;
import carehub.web.dto.auth.AuthRequestResponse.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuth2Service {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${spring.kakao.client-id}")
    private String clientId;

    @Value("${spring.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.kakao.redirect-uri}")
    private String redirectUri;

    @Transactional
    public TokenResponse loginWithKakao(String code, String deviceId, String fcmToken) {
        String kakaoAccessToken = getKakaoAccessToken(code);
        Map<String, Object> userAttributes = getKakaoUserInfo(kakaoAccessToken);
        log.info("userAttributes={}", userAttributes);
        Long kakaoId = (Long) userAttributes.get("id");

        if (kakaoId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "카카오 사용자 정보를 가져올 수 없습니다.");
        }

        log.info("Kakao login - User ID: {}", kakaoId);

        User user = userRepository.findBySocialProviderAndSocialId("kakao", kakaoId.toString())
                .orElseGet(() -> createKakaoUser(userAttributes));

        return authService.createTokensForSocialLogin(user, deviceId, fcmToken);
    }

    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        log.info("kakaoTokenRequest.getHeaders()={}", kakaoTokenRequest.getHeaders());
        log.info("kakaoTokenRequest.getBody()={}", kakaoTokenRequest.getBody());
        try {
            log.debug("Sending request to Kakao: {}", params);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    Map.class
            );

            log.info("Kakao token response: {}", response.getBody());
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException e) {
            log.error("Failed to get Kakao access token. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.UNAUTHORIZED,
                    "카카오 인증에 실패했습니다: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to get Kakao access token: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "카카오 인증에 실패했습니다.");
        }
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

        log.info("Kakao user info kakaoUserInfoRequest: {}", kakaoUserInfoRequest);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    kakaoUserInfoRequest,
                    Map.class
            );
            log.info("Kakao user info response: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get Kakao user info", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "카카오 사용자 정보를 가져올 수 없습니다.");
        }
    }

    @Transactional
    public User createKakaoUser(Map<String, Object> attributes) {
        Long kakaoId = (Long) attributes.get("id");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String nickname = properties != null ? (String) properties.get("nickname") : "사용자" + kakaoId;
        String profileImageUrl = properties != null ? (String) properties.get("profile_image") : null;

        String tempEmail = "kakao_" + kakaoId + "@carehub.temp";

        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        User user = User.builder()
                .email(tempEmail)
                .password(encodedPassword)
                .name(nickname)
                .profileImageUrl(profileImageUrl)
                .socialProvider("kakao")
                .socialId(kakaoId.toString())
                .isActive(true)
                .build();

        UserPreference preference = new UserPreference();
        preference.setUser(user);
        preference.setTheme("light");
        preference.setLanguage("ko");
        user.setPreference(preference);

        log.info("Created new user from Kakao: {}", tempEmail);

        return userRepository.save(user);
    }
}