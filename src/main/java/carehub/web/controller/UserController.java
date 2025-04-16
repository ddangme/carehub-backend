package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.domain.user.UserService;
import carehub.web.dto.auth.request.PasswordResetRequest;
import carehub.web.dto.auth.request.PasswordResetVerifyRequest;
import carehub.web.dto.auth.request.RegisterUserRequest;
import carehub.web.dto.auth.response.PasswordResetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * @param request 회원가입 요청 객체
     * @return API 응답
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> registerUser(
            @Valid @RequestBody RegisterUserRequest request) {
        Long userId = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(userId));
    }

    /**
     * 비밀번호 재설정 요청 (이메일 발송)
     * @param request 비밀번호 재설정 요청 정보
     * @return API 응답
     */
    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request) {
        userService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 비밀번호 재설정 토큰 검증
     * @param request 토큰 검증 요청 정보
     * @return 토큰 검증 결과
     */
    @PostMapping("/password-reset/verify-token")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> verifyPasswordResetToken(
            @Valid @RequestBody PasswordResetVerifyRequest request) {
        boolean isValid = userService.verifyPasswordResetToken(
                request.getEmail(),
                request.getToken()
        );

        return ResponseEntity.ok(ApiResponse.success(
                PasswordResetResponse.builder()
                        .verified(isValid)
                        .build()
        ));
    }

    /**
     * 새 비밀번호 설정
     * @param request 비밀번호 재설정 요청 정보
     * @return API 응답
     */
    @PostMapping("/password-reset/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {
        userService.resetPassword(
                request.getEmail(),
                request.getToken(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(ApiResponse.success());
    }
}
