package carehub.web.controller;

import carehub.common.dto.response.ApiResponse;
import carehub.domain.user.UserService;
import carehub.web.dto.auth.request.SendVerificationRequest;
import carehub.web.dto.auth.request.VerifyCodeRequest;
import carehub.web.dto.auth.response.EmailVerificationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/verification/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final UserService userService;

    /**
     * 이메일 중복 확인 API
     * @param email 확인할 이메일
     * @return 중복 여부 응답
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        log.info("email={}", email);
        boolean isDuplicate = userService.isEmailExists(email);
        return ResponseEntity.ok(ApiResponse.success(!isDuplicate));
    }

    /**
     * 이메일 인증 코드 발송 API
     * @param request 이메일 정보를 담은 요청 객체
     * @return API 응답
     */
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(
            @Valid @RequestBody SendVerificationRequest request) {
        userService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 이메일 인증 코드 확인 API
     * @param request 인증 코드 확인 요청 객체
     * @return 인증 결과 및 토큰 응답
     */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<EmailVerificationResponse>> verifyEmailCode(
            @Valid @RequestBody VerifyCodeRequest request) {
        String verificationToken = userService.verifyEmailCode(
                request.getEmail(),
                request.getVerificationCode()
        );

        return ResponseEntity.ok(ApiResponse.success(
                EmailVerificationResponse.builder()
                        .verified(true)
                        .verificationToken(verificationToken)
                        .build()
        ));
    }
}