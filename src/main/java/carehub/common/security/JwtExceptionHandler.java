package carehub.common.security;

import carehub.common.dto.response.ApiResponse;
import carehub.common.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("Expired JWT token: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.EXPIRED_TOKEN, "만료된 토큰입니다. 다시 로그인해주세요."));
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedJwtException(UnsupportedJwtException e) {
        log.error("Unsupported JWT token: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.INVALID_TOKEN, "지원되지 않는 토큰 형식입니다."));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJwtException(MalformedJwtException e) {
        log.error("Malformed JWT token: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.INVALID_TOKEN, "잘못된 토큰 형식입니다."));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<Void>> handleSignatureException(SignatureException e) {
        log.error("Invalid JWT signature: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰 서명입니다."));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException e) {
        log.error("JWT token error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다."));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication error: {}", e.getMessage());

        if (e instanceof BadCredentialsException) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ErrorCode.INVALID_PASSWORD, "이메일 또는 비밀번호가 올바르지 않습니다."));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "인증에 실패했습니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.ACCESS_DENIED, "접근 권한이 없습니다."));
    }
}