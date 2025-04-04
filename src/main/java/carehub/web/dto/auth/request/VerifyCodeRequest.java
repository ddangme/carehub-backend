package carehub.web.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "인증 코드는 필수 입력값입니다")
    @Size(min = 6, max = 6, message = "인증 코드는 6자리여야 합니다")
    private String verificationCode;
}
