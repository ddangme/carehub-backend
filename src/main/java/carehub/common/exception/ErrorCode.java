package carehub.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 공통 오류
    INVALID_INPUT(400, "C001", "잘못된 입력값입니다"),
    UNAUTHORIZED(401, "C002", "인증이 필요합니다"),
    FORBIDDEN(403, "C003", "접근 권한이 없습니다"),
    RESOURCE_NOT_FOUND(404, "C004", "요청한 리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(500, "C005", "서버 내부 오류가 발생했습니다"),

    // 사용자 관련 오류
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(400, "U002", "이미 사용 중인 이메일입니다"),

    // 케어 대상 관련 오류
    CARE_TARGET_NOT_FOUND(404, "CT001", "케어 대상을 찾을 수 없습니다"),

    // 케어 활동 관련 오류
    ACTIVITY_NOT_FOUND(404, "A001", "케어 활동을 찾을 수 없습니다"),
    INVALID_ACTIVITY_TYPE(400, "A002", "유효하지 않은 활동 유형입니다");

    private final int status;
    private final String code;
    private final String message;

}
