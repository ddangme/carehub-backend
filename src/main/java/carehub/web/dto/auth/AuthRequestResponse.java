package carehub.web.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthRequestResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다")
        @Email(message = "유효한 이메일 형식이 아닙니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        private String password;

        private String deviceId;

        private String fcmToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private String tokenType;
        private UserInfo userInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        @NotBlank(message = "리프레시 토큰은 필수 입력값입니다")
        private String refreshToken;

        private String deviceId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String profileImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutRequest {
        private String deviceId;
    }
}