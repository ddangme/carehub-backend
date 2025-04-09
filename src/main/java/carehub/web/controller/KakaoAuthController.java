package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.domain.security.oauth2.KakaoOAuth2Service;
import carehub.web.dto.auth.AuthRequestResponse.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoOAuth2Service kakaoOAuth2Service;

    /**
     * 카카오 로그인 콜백 처리 - GET 메서드로 변경
     * @param code 카카오 인증 코드
     * @param deviceId 기기 ID (선택)
     * @param fcmToken FCM 토큰 (선택)
     * @return API 응답 (토큰 정보)
     */
    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoCallback(
            @RequestParam String code,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String fcmToken) {

        log.info("Received Kakao login callback with code: {}", code);

        TokenResponse tokenResponse = kakaoOAuth2Service.loginWithKakao(code, deviceId, fcmToken);

        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}