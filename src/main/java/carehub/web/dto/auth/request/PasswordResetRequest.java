package carehub.web.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
public class PasswordResetRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;

    // 토큰은 비밀번호 재설정 시에만 필수
    private String token;

    // 새 비밀번호는 비밀번호 재설정 시에만 필수
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    private String newPassword;
}