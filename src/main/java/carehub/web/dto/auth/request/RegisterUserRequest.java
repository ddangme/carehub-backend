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
public class RegisterUserRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다")
    private String name;


    @Pattern(regexp = "^(01[016789])-?\\d{3,4}-?\\d{4}$",
            message = "유효한 전화번호 형식이 아닙니다. (예: 010-1234-5678)",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String phone;

    @NotBlank(message = "이메일 인증 시간이 초과되었습니다. 처음부터 다시 시도해주세요.")
    private String verificationToken;
}
