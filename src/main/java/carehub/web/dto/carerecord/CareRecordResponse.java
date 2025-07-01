package carehub.web.dto.carerecord;

import carehub.domain.carerecord.CareRecord;
import carehub.domain.carerecord.CareRecordType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CareRecordResponse {
    private Long id;
    private Long careSubjectId;
    private String careSubjectName;
    private CareRecordType recordType;
    private String recordTypeDisplayName;
    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordedAt;

    private Map<String, Object> recordData;
    private String recordedByName;
    private Long recordedById;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static CareRecordResponse from(CareRecord careRecord) {
        return CareRecordResponse.builder()
                .id(careRecord.getId())
                .careSubjectId(careRecord.getCareSubject().getId())
                .careSubjectName(careRecord.getCareSubject().getName())
                .recordType(careRecord.getRecordType())
                .recordTypeDisplayName(careRecord.getRecordType().getDisplayName())
                .title(careRecord.getTitle())
                .description(careRecord.getDescription())
                .recordedAt(careRecord.getRecordedAt())
                .recordData(careRecord.getRecordData())
                .recordedByName(careRecord.getRecordedBy().getName())
                .recordedById(careRecord.getRecordedBy().getId())
                .createdAt(careRecord.getCreatedAt())
                .updatedAt(careRecord.getUpdatedAt())
                .build();
    }
}
