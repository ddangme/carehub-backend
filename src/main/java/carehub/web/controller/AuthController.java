package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.domain.security.AuthService;
import carehub.web.dto.auth.AuthRequestResponse.LoginRequest;
import carehub.web.dto.auth.AuthRequestResponse.LogoutRequest;
import carehub.web.dto.auth.AuthRequestResponse.RefreshTokenRequest;
import carehub.web.dto.auth.AuthRequestResponse.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse response = authService.login(
                request.getEmail(),
                request.getPassword(),
                request.getDeviceId(),
                request.getFcmToken()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 재발급 API
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        TokenResponse response = authService.refreshToken(
                request.getRefreshToken(),
                request.getDeviceId()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody LogoutRequest request) {

        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User principal =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            Long userId = Long.parseLong(principal.getUsername());
            authService.logout(accessToken, null, userId, request.getDeviceId());
        }


        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 모든 기기에서 로그아웃 API
     */
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutFromAllDevices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User principal =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            Long userId = Long.parseLong(principal.getUsername());

            authService.logoutFromAllDevices(userId);
        }

        return ResponseEntity.ok(ApiResponse.success());
    }
}