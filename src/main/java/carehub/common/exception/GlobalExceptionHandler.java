package carehub.common.exception;

import carehub.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError error : result.getFieldErrors()) {
            errorMessage.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append(", ");
        }

        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errorMessage.toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        ApiResponse<Void> response = ApiResponse.error(ex.getErrorCode(), ex.getMessage()
        );

        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
