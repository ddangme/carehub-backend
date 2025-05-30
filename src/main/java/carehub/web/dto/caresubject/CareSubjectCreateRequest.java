package carehub.web.dto.caresubject;

import carehub.domain.caresubject.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

// 케어 대상 생성 요청 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareSubjectCreateRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "생년월일은 필수입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate birthDate;

    @NotNull(message = "성별은 필수입니다")
    private Gender gender;

    private String bloodType;
    private BigDecimal birthWeight;
    private BigDecimal birthHeight;
    private String profileImageUrl;
    private Map<String, Object> additionalInfo;
}

