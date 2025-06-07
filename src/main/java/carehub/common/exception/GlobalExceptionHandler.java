package carehub.common.exception;

import carehub.common.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("=== 유효성 검증 실패 ===");

        BindingResult result = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError error : result.getFieldErrors()) {
            log.error("필드 오류 - 필드: {}, 입력값: {}, 메시지: {}",
                    error.getField(), error.getRejectedValue(), error.getDefaultMessage());

            errorMessage.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append(", ");
        }

        String finalErrorMessage = errorMessage.toString();
        if (finalErrorMessage.endsWith(", ")) {
            finalErrorMessage = finalErrorMessage.substring(0, finalErrorMessage.length() - 2);
        }

        log.error("최종 오류 메시지: {}", finalErrorMessage);

        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, finalErrorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // JSON 파싱 오류 처리 (Gender enum 변환 오류 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("=== JSON 파싱 오류 발생 ===");
        log.error("원본 오류: {}", ex.getMessage());

        String errorMessage = "잘못된 요청 데이터입니다.";

        // 구체적인 오류 원인 파악
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String fieldName = getFieldName(ife);
            Object invalidValue = ife.getValue();
            Class<?> targetType = ife.getTargetType();

            log.error("잘못된 형식 오류 - 필드: {}, 입력값: {}, 대상 타입: {}",
                    fieldName, invalidValue, targetType.getSimpleName());

            // Gender enum 관련 오류인 경우
            if (targetType.isEnum() && fieldName.contains("gender")) {
                errorMessage = String.format("성별 값이 올바르지 않습니다. '%s'는 유효하지 않은 값입니다. (유효한 값: M, F, O)", invalidValue);
            } else if (targetType.isEnum()) {
                errorMessage = String.format("'%s' 필드의 값 '%s'가 올바르지 않습니다.", fieldName, invalidValue);
            } else {
                errorMessage = String.format("'%s' 필드의 형식이 올바르지 않습니다. 입력값: '%s'", fieldName, invalidValue);
            }
        } else if (cause instanceof JsonMappingException) {
            JsonMappingException jme = (JsonMappingException) cause;
            String fieldName = getFieldName(jme);
            log.error("JSON 매핑 오류 - 필드: {}", fieldName);
            errorMessage = String.format("'%s' 필드를 처리하는 중 오류가 발생했습니다.", fieldName);
        }

        // Gender 관련 오류 메시지가 포함된 경우 특별 처리
        if (ex.getMessage().contains("Gender") || ex.getMessage().contains("gender")) {
            errorMessage = "성별 정보가 올바르지 않습니다. 성별을 선택해주세요. (남성, 여성, 기타 중 선택)";
        }

        log.error("최종 오류 메시지: {}", errorMessage);

        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // IllegalArgumentException 처리 (Gender.fromCode에서 발생하는 오류 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("=== 잘못된 인수 오류 발생 ===");
        log.error("오류 메시지: {}", ex.getMessage());

        String errorMessage = ex.getMessage();

        // Gender 관련 오류인 경우 사용자 친화적 메시지로 변경
        if (errorMessage.contains("Unknown gender code")) {
            errorMessage = "성별 정보가 올바르지 않습니다. 성별을 다시 선택해주세요.";
        }

        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.error("=== 비즈니스 예외 발생 ===");
        log.error("오류 코드: {}, 오류 메시지: {}", ex.getErrorCode().getCode(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("=== 예상치 못한 예외 발생 ===");
        log.error("예외 타입: {}", ex.getClass().getSimpleName());
        log.error("예외 메시지: {}", ex.getMessage());
        log.error("상세 스택 트레이스:", ex);

        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * JsonMappingException에서 필드명 추출
     */
    private String getFieldName(JsonMappingException ex) {
        if (ex.getPath() != null && !ex.getPath().isEmpty()) {
            return ex.getPath().get(ex.getPath().size() - 1).getFieldName();
        }
        return "unknown";
    }

    /**
     * InvalidFormatException에서 필드명 추출
     */
    private String getFieldName(InvalidFormatException ex) {
        if (ex.getPath() != null && !ex.getPath().isEmpty()) {
            return ex.getPath().get(ex.getPath().size() - 1).getFieldName();
        }
        return "unknown";
    }
}