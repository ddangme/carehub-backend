package carehub.web.dto.carerecord;

import carehub.domain.carerecord.CareRecordType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareRecordCreateRequest {
    @NotNull(message = "케어 대상 ID는 필수입니다")
    private Long careSubjectId;

    @NotNull(message = "기록 유형은 필수입니다")
    private CareRecordType recordType;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    private String description;

    @NotNull(message = "기록 시간은 필수입니다")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordedAt;

    private Map<String, Object> recordData;
}
