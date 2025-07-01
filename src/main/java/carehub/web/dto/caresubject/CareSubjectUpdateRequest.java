package carehub.web.dto.caresubject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

// 케어 대상 수정 요청 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareSubjectUpdateRequest {
    private String name;
    private String bloodType;
    private BigDecimal birthWeight;
    private BigDecimal birthHeight;
    private String profileImageUrl;
    private Map<String, Object> additionalInfo;
}
