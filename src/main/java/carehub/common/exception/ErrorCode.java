package carehub.common.exception;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 타입입니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다"),

    // 인증 오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증되지 않은 접근입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다"),

    // 회원 오류
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U001", "이미 사용 중인 이메일입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "사용자를 찾을 수 없습니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "잘못된 비밀번호입니다"),

    // 검증 오류
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "V001", "유효하지 않은 인증 코드입니다"),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "V002", "만료된 인증 코드입니다"),
    INVALID_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "V003", "유효하지 않은 인증 토큰입니다"),
    EXPIRED_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "V004", "만료된 인증 토큰입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
