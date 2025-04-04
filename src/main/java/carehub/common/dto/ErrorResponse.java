package carehub.common.dto;

import carehub.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode code;
    private String message;
}
